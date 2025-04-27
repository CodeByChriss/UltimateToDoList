import java.io.*;

public class Main {
	
	/*
	 * Constantes que se van a utilizar en el programa.
	 */
	private static final char porHacer = ' ';
	private static final char completado = '\u00d8'; // Ø
	
	/*
	 * Array que va a contener todas las tareas de esta sesión.
	 * Declaro el array a 0 ya que existe la posibilidad de que
	 * si no se carga sesión, en el método visualizarTareas() 
	 * se intente leer el array sin declarar, lo que causaría
	 * un error.
	 */
	private static Tarea []tareas = new Tarea[0];
	
	public static void main(String[]args) throws IOException {
		BufferedReader lector = new BufferedReader(new InputStreamReader(System.in));
		
		System.out.println("BIENVENIDO A ULTIMATE TO-DO LIST");
		
		// Preguntamos al usuario si tiene alguna sesión anterior para cargar.
		sesionAnterior(lector);
		
		visualizarTareas();
		visualizarMenu();
		
		System.out.println("Programa finalizado.");
	}
	
	public static void sesionAnterior(BufferedReader lector) throws IOException {
		char respuesta = 'N';
		int errores = 0; // va a contener la cantidad de veces que un usuario introduce una opción no válida
		
		do {
			System.out.print("¿Tienes alguna sesi\u00f3n guardada? Por favor, introduzca SI o NO: ");
			respuesta = lector.readLine().toUpperCase().charAt(0);
			if(respuesta == 'S') {
				errores = 3; // salimos del bucle
				cargarSesion(lector);
			}else{
				errores ++;
			}
		}while(respuesta != 'N' && errores < 3); // 3 es la cantidad máxima que le permitimos al usuario equivocarse hasta dar por hecho que no tiene una sesió anterior
	}
	
	public static void cargarSesion(BufferedReader lector) throws IOException {
		String ruta;
		File sesionAntigua;
		FileInputStream fis;
		DataInputStream dis;
		BufferedReader contarLineas;
		String nombre;
		int estado, cntLineas, index;
		char repetir = 'N', basura;
		
		do {
			repetir = 'N';
			System.out.println("Introduce la ruta en la que tienes el fichero de la sesi\u00f3n anterior (incluido el nombre y extensi\u00f3n del fichero).");
			ruta = lector.readLine();
			
			sesionAntigua = new File(ruta);
			if(sesionAntigua.isFile()) {
				fis = new FileInputStream(sesionAntigua);
				dis = new DataInputStream(fis);
				
				// Contamos la cantidad de lineas que hay en fichero (una linea = una tarea)
				contarLineas = new BufferedReader(new FileReader(sesionAntigua));
				cntLineas=0;
				while(contarLineas.readLine()!=null) {
					cntLineas++;
				}
				tareas = new Tarea[cntLineas];
				
				// Leemos el fichero y creamos las tareas
				index = 0;
				
				while(index < cntLineas) {
					estado = dis.readInt();
					nombre = dis.readUTF();
					basura = dis.readChar(); // debemos leer el salto de linea para evitar problemas
					
					tareas[index] = new Tarea(estado, nombre);
					index++;
				}
			}else {
				System.out.println("Ha ocurrido un error durante el proceso. ¿Quieres volver a repetirlo? SI o NO");
				repetir = lector.readLine().toUpperCase().charAt(0);
			}			
		}while(repetir != 'N');
	}
	
	public static void visualizarTareas() {
		if(tareas.length > 0) {
			boolean todasCompletadas = true; // si estan todas completadas se mostrará un mensaje especial.
			
			System.out.println("Tus tareas actuales son: \n");
			for(int i = 0; i<tareas.length;i++) {
				if( todasCompletadas && tareas[i].getEstado() == 0)
					todasCompletadas = false;
				
				System.out.println("\t [" + (tareas[i].getEstado() == 0 ? porHacer : completado) + "] \t " + tareas[i].getNombre() + "\n");
			}
			
			if(todasCompletadas) {
				System.out.println("\t Parece que has completado todas tus tareas, ¡ENHORABUENA! \n \t ¿A qu\u00e9 esperas para empezar tu siguiente desafio?");
			}
		}else {
			System.out.println("\n\t No hay tareas pendientes. Agrega una para empezar.");
		}
	}

	public static void visualizarMenu(){
		System.out.println("\n \t Men\u00fa de opcines: \n"
				+ "\t .......................................................... \n"
				+ "\t\t 1 - Agregar nueva tarea \n"
				+ "\t\t 2 - Modificar tarea existente \n"
				+ "\t\t 3 - Mostrar frase motivadora \n"
				+ "\t\t 4 - Guardar sesi\u00f3n actual \n"
				+ "\t\t 5 - Cerrar aplicaci\u00f3n \n");
	}
}
