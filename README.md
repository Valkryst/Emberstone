# References
* https://github.com/Xyene/Nitrous-Emulator - Faster rendering

# Todo

Add support for OSX and test support for Linux. Original attempt at OSX support below with a few links that were used.

* https://github.com/AdoptOpenJDK/openjdk-jdk11/tree/master/src/java.desktop/macosx/classes/sun/lwawt
* 

```java
package com.valkryst.Emberstone.display.renderer;

import java.awt.*;
import java.awt.peer.ComponentPeer;

// OSX Specific
// https://github.com/AdoptOpenJDK/openjdk-jdk11/tree/master/src/java.desktop/macosx/classes/sun/java2d/opengl
public class CGLRenderer extends Renderer {
    public final static String PEER_CLASS_NAME = "sun.lwawt.LWComponentPeer";
    public final static String SURFACE_CLASS_NAME = "sun.java2d.opengl.CGLSurfaceData";

    public CGLRenderer(final ComponentPeer peer) throws UnsupportedOperationException {
        super(peer, CGLRenderer.class);
    }

    @Override
    public GraphicsConfiguration getGraphicsConfig() {
        try {
            final var currentGraphicsConfiguration = super.getGraphicsConfig();

            final var cGraphicsDevice = Class.forName("sun.awt.CGraphicsDevice");
            final var cGraphicsConfig = Class.forName("sun.awt.CGraphicsConfig");
            final var cglGraphicsConfig = Class.forName("sun.java2d.opengl.CGLGraphicsConfig");

            final var device = cGraphicsConfig.getMethod("getDevice").invoke(currentGraphicsConfiguration);

            final var displayIdField = cGraphicsDevice.getDeclaredField("displayID");
            displayIdField.setAccessible(true);
            final int displayId = (int) displayIdField.get(device);

            final var getConfigMethod = cglGraphicsConfig.getMethod("getConfig", cglGraphicsConfig, int.class);

            return (GraphicsConfiguration) getConfigMethod.invoke(null, currentGraphicsConfiguration.getDevice(), displayId);
        } catch (final Exception e) {
            // todo Tons of exceptions can be thrown in the above code, catch it or something.
            // Otherwise, we fall back to the default and hope for the best.
            e.printStackTrace();
            return super.getGraphicsConfig();
        }
    }

    @Override
    public String getName() {
        return "OSX OpenGL";
    }
}
```