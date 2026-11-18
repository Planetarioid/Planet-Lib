# Release Readiness Checklist

This checklist distinguishes repository and automated-build readiness from runtime validation and public release decisions.

## Repository quality

- [x] All 11 planned API systems have implementation files.
- [x] Each system has architecture documentation.
- [x] Unit tests cover the core value objects and registries.
- [x] GitHub Actions runs a clean Gradle build and JUnit tests.
- [x] CI verifies the packaged mod JAR contains its Fabric metadata and main entrypoint.
- [x] CI uploads the built JAR, sources JAR, and test report as workflow artifacts.
- [x] README and API usage guide describe the supported target and API boundaries.

## Required before a public stable release

- [ ] Choose and add a project license. No license has been assumed on the owner's behalf.
- [ ] Run Planet Lib in a clean Minecraft 1.21.11 Fabric client.
- [ ] Run a dedicated-server startup test.
- [ ] Verify the optional Planet Essentials bridge with Essentials absent, present, incompatible, and with a deliberately failing test entrypoint.
- [ ] Test a small dependent Planet mod against the published API surface.
- [ ] Confirm the consuming mod can resolve Planet Lib as a Fabric dependency and invoke each API.
- [ ] Record actual Minecraft client/server test results and supported loader versions.
- [ ] Decide whether to publish to Modrinth, CurseForge, GitHub Releases, or a Maven repository.
- [ ] Create a version tag and release notes after the above checks pass.

## Verification language

A passing GitHub Actions build proves that the code compiles, unit tests pass, and the expected JAR structure is present. It does not prove in-game behavior, server compatibility, integration with Planet Essentials, or compatibility with third-party mods. Those require runtime tests on the exact versions being claimed.
