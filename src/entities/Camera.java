package entities;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class Camera {

	private static final float MIN_CAMERA_DISTANCE = 10;
	private static final float MIN_CAMERA_PITCH = 10;
	private static final float MAX_CAMERA_PITCH = 90;
	private static final float CAMERA_HEIGHT_OFFSET = 5;
	
	public static final float SKY_RED = 0.5f;
	public static final float SKY_GREEN = 0.5f;
	public static final float SKY_BLUE = 0.5f;
	
	private float distanceFromPlayer = 50;
	private float angleAroundPlayer = 0;

	private Vector3f position = new Vector3f(0, 0, 0);
	private float pitch = 20;
	private float yaw = 0;
	private float roll;

	private Player player;

	public Camera(Player player) {
		this.player = player;
	}

	public void move() {
		calculateZoom();
		calculatePitch();
		calculateAngleAroundPlayer();
		float horizontalDistance = calculateHorizontalDistance();
		float verticalDistance = calculateVerticalDistance();
		calculateCameraPosition(horizontalDistance, verticalDistance);
		this.yaw = 180 - (player.getRotY() + angleAroundPlayer);
	}

	public Vector3f getPosition() {
		return position;
	}

	public float getPitch() {
		return pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public float getRoll() {
		return roll;
	}

	private void calculateCameraPosition(float horizDistance, float verticDistance) {
		float theta = player.getRotY() + angleAroundPlayer;
		float offsetX = (float) (horizDistance * Math.sin(Math.toRadians(theta)));
		float offsetZ = (float) (horizDistance * Math.cos(Math.toRadians(theta)));
		position.x = player.getPosition().x - offsetX;
		position.y = player.getPosition().y + verticDistance + CAMERA_HEIGHT_OFFSET;
		position.z = player.getPosition().z - offsetZ;
	}

	private float calculateHorizontalDistance() {
		return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
	}

	private float calculateVerticalDistance() {
		return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
	}

	private void calculateZoom() {
		float zoomLevel = Mouse.getDWheel() * 0.1f;
		distanceFromPlayer -= zoomLevel;
		if (distanceFromPlayer < MIN_CAMERA_DISTANCE) {
			distanceFromPlayer = MIN_CAMERA_DISTANCE;
		}
	}

	private void calculatePitch() {
		if (Mouse.isButtonDown(1)) {
			float pitchChange = Mouse.getDY() * 0.1f;
			pitch -= pitchChange;
			if (pitch < MIN_CAMERA_PITCH) {
				pitch = MIN_CAMERA_PITCH;
			} else if (pitch > MAX_CAMERA_PITCH) {
				pitch = MAX_CAMERA_PITCH;
			}
		}
	}

	private void calculateAngleAroundPlayer() {
		if (Mouse.isButtonDown(0)) {
			float angleChange = Mouse.getDX() * 0.3f;
			angleAroundPlayer -= angleChange;
		}
	}
	
	public Vector3f getSkyColor() {
		return new Vector3f(SKY_RED, SKY_GREEN, SKY_BLUE);
	}

	public void invertPitch() {
		this.pitch = -pitch;
	}
	
}
