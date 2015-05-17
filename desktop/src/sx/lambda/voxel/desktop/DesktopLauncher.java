package sx.lambda.voxel.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import sx.lambda.voxel.VoxelGameClient;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.useGL30 = true;
		config.vSyncEnabled = false;
		config.foregroundFPS = 0;
		new LwjglApplication(new VoxelGameClient(), config);
	}
}