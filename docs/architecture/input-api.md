# Optional Input API

## Purpose

The Optional Input API provides a small, explicit registry for logical input-action metadata. It is opt-in: a mod must create and use an InputActionRegistry before it has any registered actions. Planet Lib does not initialize a global input engine or install input hooks automatically.

## API

- InputActionId: stable owner-namespaced identifier.
- InputActionDescriptor: display metadata, category, and an opaque default binding identifier.
- InputActionSnapshot: immutable, sorted lookup view.
- InputActionRegistry: duplicate-safe registration and snapshot publication.

## Boundaries

This first milestone is a metadata and discovery contract, not a complete keybinding backend. A client-side mod integration is responsible for connecting registered actions to the supported Minecraft/Fabric input lifecycle.

The API does not poll hardware, synthesize key presses, generate clicks, alter click timing, bypass user settings, or manipulate hit registration. It does not run when unused and introduces no background thread or per-tick work.

Future input functionality must remain explicitly activated by the consuming mod, preserve user-configured bindings, and keep physical input under the player's control.
