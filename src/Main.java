
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
		boolean continuar = true; // true -> El programa continua | false -> El programa se detiene
		boolean visualizar = true;
		int respuesta=0;
		
		System.out.println("BIENVENIDO A ULTIMATE TO-DO LIST");
		
		// Preguntamos al usuario si tiene alguna sesión anterior para cargar.
		sesionAnterior(lector);
		
		visualizarTareas();
		visualizarMenu();
		
		do {
			if(visualizar) {
				limpiarPantalla();
				visualizarTareas();
				visualizarMenu();
				visualizar = false;
			}
			respuesta = opcionElegida(lector);
			switch(respuesta) {
				case 1:
					agregarTarea(lector);
					visualizar = true;
					break;
				case 2:
					if(tareas.length > 0) {
						modificarTarea(lector);
						visualizar = true;
					}else
						System.out.println("No hay tareas que modificar");
					break;
				case 3:
					continuar = false;
					break;
			}
		}while(continuar);
		
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
				+ "\t\t 2 - Modificar Tarea \n"
				+ "\t\t 3 - Finalizar Programa \n");
	}
	
	public static int opcionElegida(BufferedReader lector) throws IOException {
		int respuesta=0;
		boolean error = true;
		
		System.out.print("¿Qu\u00e9 quieres hacer? ");
		do {
			try {
				respuesta = Integer.parseInt(lector.readLine());
				if(respuesta >= 1 && respuesta <= 5)
					error = false;
			}catch(NumberFormatException e) {
				respuesta = 0;
			}
			if(error)
				System.out.println("Esa opci\u00f3n no existe. Selecciona una opci\u00f3n v\u00e1lida: ");
		}while(error);
		
		return respuesta;
	}
	
	public static void agregarTarea(BufferedReader lector) throws IOException {
		String nombre;
		int estado;
		Tarea []tareasTmp;
		
		limpiarPantalla();
		
		System.out.print("Introduce el nombre de la nueva tarea: ");
		nombre = lector.readLine();
		
		System.out.print("¿Quieres marcar la tarea como completada? SI o NO: ");
		// Si no introduce cualquier otra cosa que no sea un SI, damos por hecho que es que un NO.
		estado = lector.readLine().toUpperCase().charAt(0) == 'S' ? 1 : 0;
		
		// Declaramos el array temporal con un +1 para luego agregar la nueva tarea
		tareasTmp = new Tarea[tareas.length+1];
		// Pasamos todas las tareas ya existentes al array temporal
		for(int i = 0; i<tareas.length;i++) {
			tareasTmp[i] = tareas[i];
		}
		tareasTmp[tareas.length] = new Tarea(estado, nombre);
		
		tareas = tareasTmp;
	}
	
	public static void modificarTarea(BufferedReader lector) throws IOException{
		String nuevoNombre;
		int respuesta=0, accion=0;
		boolean error=true;
		
		limpiarPantalla();
		
		System.out.println("Las tareas son: \n");
		
		for(int i = 0; i<tareas.length;i++) {
			System.out.println("\t" + (i+1) + ". [" + (tareas[i].getEstado() == 0 ? porHacer : completado) + "] \t " + tareas[i].getNombre() + "\n");
		}
		
		System.out.print("Tarea a modificar: ");
		do {
			try {
				respuesta = Integer.parseInt(lector.readLine());
				if(respuesta >= 1 && respuesta <= tareas.length)
					error = false;
			}catch(NumberFormatException e) {
				respuesta = 0;
			}
			if(error)
				System.out.print("Esa tarea no existe. Introduce una existente: ");
		}while(error);
		respuesta--; // se le resta 1 por que el index de un Array empieza en 0 pero lo hemos mostrado empezando por 1 
		
		System.out.println("¿Qu\u00e9 quieres hacer?\n"
				+ "\t 1 - Cambiar el nombre \n"
				+ "\t 2 - Cambiar el estado \n"
				+ "\t 3 - Cambiar el nombre y el estado \n"
				+ "\t 4 - Eliminar tarea \n");
		System.out.println("Acci\u00f3n a realizar: ");
		error = true;
		do {
			try {
				accion = Integer.parseInt(lector.readLine());
				if(accion >= 1 && accion <= 4)
					error = false;
			}catch(NumberFormatException e) {
				accion = 0;
			}
			if(error)
				System.out.print("Esa acci\u00f3n no existe. Introduce una existente: ");
		}while(error);
		
		if(accion == 1) {
			System.out.print("Introduce el nuevo nombre para la tarea: ");
			nuevoNombre = lector.readLine();
			tareas[respuesta].setNombre(nuevoNombre);
		}else if(accion == 2) {
			tareas[respuesta].setEstado(tareas[respuesta].getEstado() == 1 ? 0 : 1);
		}else if(accion == 3){
			System.out.print("Introduce el nuevo nombre para la tarea: ");
			nuevoNombre = lector.readLine();
			tareas[respuesta].setNombre(nuevoNombre);
			tareas[respuesta].setEstado(tareas[respuesta].getEstado() == 1 ? 0 : 1);
		}else{
			eliminarTarea(respuesta);
		}
	}
	
	public static void eliminarTarea(int tareaIndex) {
		Tarea []tmpTareas = new Tarea[tareas.length-1];
		boolean pasado = false;
		for(int i=0; i<tareas.length; i++ ) {
			if(i == tareaIndex) {
				pasado = true;
			}else {
				tmpTareas[pasado ? i-1 : i] = tareas[i];
			}
		}
		tareas = tmpTareas;
	}

	/*
	 * Método que nos va a permitir limpiar la pantalla de la terminal.
	 * Esto puede no funcionar para todas las terminales.
	 */
	public static void limpiarPantalla() throws IOException {
		System.out.print("\033[H\033[2J");
		System.out.flush();
	}
}
