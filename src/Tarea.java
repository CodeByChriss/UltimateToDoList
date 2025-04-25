
public class Tarea {
	
	private int estado; // 0 -> por hacer | 1 -> completada
	private String nombre;
	
	public Tarea(String nombre) {
		estado = 0;
		this.nombre = nombre;
	}
	
	public Tarea (int estado, String nombre) {
		this.estado = estado == 1 ? 1 : 0;
		this.nombre = nombre;
	}

	public int getEstado() {
		return estado;
	}

	public void setEstado(int estado) {
		this.estado = estado;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
}