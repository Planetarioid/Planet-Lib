package com.planetstudios.planetlib.feature;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
class FeaturePriorityManagerTest {
 @Test void selectsHighestPriorityImplementationDeterministically(){
  FeaturePriorityManager m=new FeaturePriorityManager(registry());FeatureId a=id("planet_alpha"),b=id("planet_beta");
  m.register(impl(a,10));m.register(impl(b,20));FeatureResolution r=m.findResolution("planet:rendering/entity").orElseThrow();
  assertEquals(b,r.selected().featureId());assertFalse(r.isUserPreferred());assertEquals(b,r.implementations().get(0).featureId());
 }
 @Test void equalPrioritiesUseFeatureIdAsStableTieBreaker(){
  FeaturePriorityManager m=new FeaturePriorityManager(registry());FeatureId a=id("planet_alpha"),b=id("planet_beta");
  m.register(impl(b,5));m.register(impl(a,5));assertEquals(a,m.findResolution("planet:rendering/entity").orElseThrow().selected().featureId());
 }
 @Test void userPreferenceOverridesPriorityAndCanBeReversed(){
  FeaturePriorityManager m=new FeaturePriorityManager(registry());FeatureId a=id("planet_alpha"),b=id("planet_beta");
  m.register(impl(a,1));m.register(impl(b,99));FeaturePrioritySnapshot p=m.preferImplementation("planet:rendering/entity",a);
  assertEquals(a,p.find("planet:rendering/entity").orElseThrow().selected().featureId());assertTrue(p.find("planet:rendering/entity").orElseThrow().isUserPreferred());
  FeaturePrioritySnapshot restored=m.clearPreference("planet:rendering/entity");
  assertEquals(b,restored.find("planet:rendering/entity").orElseThrow().selected().featureId());assertFalse(restored.find("planet:rendering/entity").orElseThrow().isUserPreferred());
 }
 @Test void rejectsUnknownFeaturesAndInvalidPreferences(){
  FeaturePriorityManager m=new FeaturePriorityManager(registry());FeatureId unknown=new FeatureId("planet_missing","renderer");
  assertThrows(IllegalArgumentException.class,()->m.register(impl(unknown,1)));
  assertThrows(IllegalArgumentException.class,()->m.preferImplementation("planet:rendering/entity",unknown));
 }
 @Test void rejectsDuplicateProvidersAndInvalidCapabilityIds(){
  FeaturePriorityManager m=new FeaturePriorityManager(registry());FeatureId a=id("planet_alpha");m.register(impl(a,1));
  assertThrows(IllegalArgumentException.class,()->m.register(impl(a,2)));
  assertThrows(IllegalArgumentException.class,()->new FeatureImplementation("Planet:Rendering",a,1));
  assertThrows(IllegalArgumentException.class,()->new FeatureImplementation("planet:rendering/../entity",a,1));
 }
 @Test void resolvesCapabilitiesIndependentlyAndPublishesImmutableSnapshots(){
  FeaturePriorityManager m=new FeaturePriorityManager(registry());m.register(impl(id("planet_alpha"),1));
  m.register(new FeatureImplementation("planet:audio/mixing",id("planet_beta"),1));FeaturePrioritySnapshot s=m.snapshot();
  assertEquals(2,s.size());assertTrue(s.find("planet:audio/mixing").isPresent());
  assertThrows(UnsupportedOperationException.class,()->s.getResolutions().clear());
 }
 @Test void lifecycleCallbacksFollowAutomaticAndManualProviderTransitions(){
  FeaturePriorityManager m=new FeaturePriorityManager(registry());FeatureId a=id("planet_alpha"),b=id("planet_beta");List<String> events=new ArrayList<>();
  m.register(impl(a,1),lifecycle("alpha",events));m.register(impl(b,2),lifecycle("beta",events));
  m.preferImplementation("planet:rendering/entity",a);m.clearPreference("planet:rendering/entity");
  assertEquals(List.of("alpha:selected","alpha:deselected","beta:selected","beta:deselected",
    "alpha:selected","alpha:deselected","beta:selected"),events);
 }
 @Test void failedActivationRollsBackSelectionAndReactivatesPreviousProvider(){
  FeaturePriorityManager m=new FeaturePriorityManager(registry());FeatureId a=id("planet_alpha"),b=id("planet_beta");List<String> events=new ArrayList<>();
  m.register(impl(a,10),lifecycle("alpha",events));
  m.register(impl(b,1),new FeatureLifecycle(){@Override public void onSelected(){throw new IllegalStateException("nope");}});
  assertThrows(IllegalStateException.class,()->m.preferImplementation("planet:rendering/entity",b));
  assertEquals(a,m.findResolution("planet:rendering/entity").orElseThrow().selected().featureId());
  assertEquals(List.of("alpha:selected","alpha:deselected","alpha:selected"),events);
 }
 @Test void failedDeactivationKeepsPreviousSelection(){
  FeaturePriorityManager m=new FeaturePriorityManager(registry());FeatureId a=id("planet_alpha"),b=id("planet_beta");
  m.register(impl(a,10),new FeatureLifecycle(){@Override public void onDeselected(){throw new IllegalStateException("busy");}});
  m.register(impl(b,1));assertThrows(IllegalStateException.class,()->m.preferImplementation("planet:rendering/entity",b));
  assertEquals(a,m.findResolution("planet:rendering/entity").orElseThrow().selected().featureId());
 }
 @Test void failedAutomaticTakeoverRestoresOldProvider(){
  FeaturePriorityManager m=new FeaturePriorityManager(registry());FeatureId a=id("planet_alpha"),b=id("planet_beta");List<String> events=new ArrayList<>();
  m.register(impl(a,1),lifecycle("alpha",events));
  assertThrows(IllegalStateException.class,()->m.register(impl(b,2),new FeatureLifecycle(){@Override public void onSelected(){throw new IllegalStateException("nope");}}));
  assertEquals(a,m.findResolution("planet:rendering/entity").orElseThrow().selected().featureId());
  assertEquals(List.of("alpha:selected","alpha:deselected","alpha:selected"),events);
 }
 @Test void oneFeatureCanImplementMultipleCapabilitiesWithIndependentLifecycles(){
  FeatureRegistry registry=registry();FeaturePriorityManager m=new FeaturePriorityManager(registry);List<String> events=new ArrayList<>();FeatureId a=id("planet_alpha");
  m.register(impl(a,10),lifecycle("first",events));
  m.register(new FeatureImplementation("planet:rendering/particles",a,10),lifecycle("second",events));
  assertEquals(List.of("first:selected","second:selected"),events);
 }
 private static FeatureLifecycle lifecycle(String n,List<String> e){return new FeatureLifecycle(){@Override public void onSelected(){e.add(n+":selected");}@Override public void onDeselected(){e.add(n+":deselected");}};}
 private static FeatureImplementation impl(FeatureId id,int priority){return new FeatureImplementation("planet:rendering/entity",id,priority);}
 private static FeatureId id(String mod){return new FeatureId(mod,"renderer");}
 private static FeatureRegistry registry(){FeatureRegistry r=new FeatureRegistry();r.register(feature("planet_alpha"));r.register(feature("planet_beta"));return r;}
 private static FeatureDescriptor feature(String mod){return new FeatureDescriptor(id(mod),"Renderer","Test renderer","rendering",Set.of("client"));}
}
