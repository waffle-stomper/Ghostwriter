# Ghostwriter.java

This is the first point of contact for Forge.
It subscribes to Forge's event bus and is responsible for swapping the vanilla book screens for Ghostwriter versions.

# gui > GhostLayer.java

Contains Ghostwriter buttons & methods to supplement the vanilla book interfaces

# gui > screen > GhostWriter*Screen.java

These extend the vanilla book screens, adding a GhostLayer to each
