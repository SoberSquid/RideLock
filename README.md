**RideLock**

RideLock is a lightweight client-side Forge mod for Minecraft 1.12.2 designed to help keep your camera perspective in line with vehicles.

It was heavily inspired by the camera-locking logic in SmoothCoasters. While modern versions of Minecraft have great options for this, 1.12.2 was lacking an option for this.
What it does

When you're sitting in a moving vehicle (minecarts, trains, etc.), RideLock calculates the motion of the entity and smoothly interpolates your camera to match the pitch and yaw of the track.

**Features**

* Smooth LERPing: Uses linear interpolation to ensure the camera transitions feel natural, not rigid.
* Toggleable: Hit F9 (default) to turn the lock on or off instantly.
* Client-Side Only: This does not need to be installed on the server.

Special thanks to Bergerhealer for the inspiration via SmoothCoasters (https://github.com/bergerhealer/SmoothCoasters/)
