package renderer;

public class CameraMovement {	
	public double speed = 1;
	
	Vector3 getForwardVector() {
		Vector3 forwardVector = new Vector3(0, 0, 0);
		Vector3 cameraRotation = Main.camera.rotation.clone();
		
		forwardVector.x = Math.cos(Math.toRadians(cameraRotation.x)) * Math.sin(Math.toRadians(cameraRotation.y));
		forwardVector.y = -Math.sin(Math.toRadians(cameraRotation.x));
		forwardVector.z = Math.cos(Math.toRadians(cameraRotation.x)) * Math.cos(Math.toRadians(cameraRotation.y));
		
		return forwardVector;
	}
	
	Vector3 getRightVector() {
		Vector3 rightVector = new Vector3(0, 0, 0);
		Vector3 cameraRotation = Main.camera.rotation.clone();
		cameraRotation.y += 90;
		
		rightVector.x = Math.cos(Math.toRadians(cameraRotation.z)) * Math.sin(Math.toRadians(cameraRotation.y));
		rightVector.y = -Math.sin(Math.toRadians(cameraRotation.z));
		rightVector.z = Math.cos(Math.toRadians(cameraRotation.z)) * Math.cos(Math.toRadians(cameraRotation.y));
				
		return rightVector;
	}
	
	Vector3 getUpVector() {
		Vector3 rightVector = new Vector3(0, 0, 0);
		Vector3 cameraRotation = Main.camera.rotation.clone();
		cameraRotation.x += 90;
		
		rightVector.x = Math.cos(Math.toRadians(cameraRotation.x)) * Math.sin(Math.toRadians(cameraRotation.y));
		rightVector.y = -Math.sin(Math.toRadians(cameraRotation.x));
		rightVector.z = Math.cos(Math.toRadians(cameraRotation.x)) * Math.cos(Math.toRadians(cameraRotation.y));
				
		return rightVector;
	}

		
	public void move(Vector3 inputAxis) {
		Vector3 forwardVector = getForwardVector();
		Vector3 rightVector = getRightVector();
		Vector3 upVector = getUpVector();
		
		for (int i = 0; i < 10; i++) {
//			Main.camera.position = Main.camera.position.add(forwardVector.multiply(Main.deltaTime * speed * inputAxis.z/10));
//			Main.camera.position = Main.camera.position.add(rightVector.multiply(Main.deltaTime * speed * inputAxis.x/10));
//			Main.camera.position = Main.camera.position.add(upVector.multiply(Main.deltaTime * speed * inputAxis.y/10));
			Main.camera.position = Main.camera.position.add(forwardVector.multiply(speed * inputAxis.z/10));
			Main.camera.position = Main.camera.position.add(rightVector.multiply(speed * inputAxis.x/10));
			Main.camera.position = Main.camera.position.add(upVector.multiply(speed * inputAxis.y/10));
		}
	}
}