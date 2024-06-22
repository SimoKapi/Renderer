package renderer;

public class Camera {
	Vector3 position, rotation;
	Vector2 resolution;
	
	Camera(Vector3 position, Vector3 rotation, Vector2 resolution) {
		this.position = position;
		this.rotation = rotation;
		this.resolution = resolution;
	}
}
