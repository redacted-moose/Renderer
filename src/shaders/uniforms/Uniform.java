package shaders.uniforms;

public abstract class Uniform {

	protected final String name;
	
	protected int handle;

	public Uniform(String name, int handle) {
		this.name = name;
		this.handle = handle;
	}

	public void load(Object data) {
		throw new IllegalArgumentException("Unknown data: " + data);
	}

	public String getName() {
		return name;
	}

	public int getHandle() {
		return handle;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
}
