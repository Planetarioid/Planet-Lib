package com.planetstudios.planetlib.feature;
/**
 * Provider-owned callbacks for applying and releasing one implementation.
 * Callbacks must be quick, safe during initialization/settings changes, and
 * must not re-enter FeaturePriorityManager. Release only resources owned by
 * this provider. The manager calls onDeselected before onSelected during a
 * transition. If activation fails, the previous selection is restored where
 * possible.
 */
public interface FeatureLifecycle {
 FeatureLifecycle NONE=new FeatureLifecycle(){};
 default void onSelected(){}
 default void onDeselected(){}
}
