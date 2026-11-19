# Feature Priority Manager
The manager resolves multiple registered implementations of one shared capability. It never enables/disables mods, installs hooks, or edits another mod's settings.

## Provider contract
A provider registers its descriptor with `FeatureRegistry`, then registers its implementation with `FeaturePriorityManager`. To participate in runtime switching it supplies a `FeatureLifecycle` with `onSelected()` and `onDeselected()`. The metadata-only overload remains supported but does not activate/deactivate runtime behavior.

Providers own their resources and must release only their own behavior. Callbacks must be quick and must not re-enter the manager.

## Transition guarantees
- Highest priority wins automatically, then stable FeatureId order.
- Preferences affect one capability only.
- The previous provider is deselected before the next provider is selected.
- Failed deactivation leaves the current selection unchanged.
- Failed activation restores the prior preference and attempts to reactivate the previous provider.
- A feature may implement multiple capabilities; each capability has an independent lifecycle.
- No entire mod is enabled or disabled by this API.
