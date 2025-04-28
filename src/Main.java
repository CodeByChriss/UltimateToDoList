package ParaCommits;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class Main {
	
	/*
	 * Constantes que se van a utilizar en el programa.
	 */
	private static final String ficheroFrasesMot = "../resources/frases_motivadoras.txt";
	private static final char porHacer = ' ';
	private static final char completado = '\u00d8'; // Ø
	
	/*
	 * Array que va a contener todas las tareas de esta sesión.
	 * Declaro el array a 0 ya que existe la posibilidad de que
	 * si no se carga sesión, en el método visualizarTareas() 
	 * se intente leer el array sin declarar, lo que causaría
	 * un error.
	 */
	private static ArrayList<Tarea> tareas = new ArrayList<Tarea>();
	
	public static void main(String[]args) throws IOException {
		BufferedReader lector = new BufferedReader(new InputStreamReader(System.in));
		boolean continuar = true; // true -> El programa continua | false -> El programa se detiene
		boolean visualizar = true;
		int respuesta=0;
		
		limpiarPantalla();
		
		System.out.println("BIENVENIDO A ULTIMATE TO-DO LIST");
		
		// Preguntamos al usuario si tiene alguna sesión anterior para cargar.
		sesionAnterior(lector);
		
		limpiarPantalla();
		visualizarTareas();
		visualizarMenu();

		do {
			respuesta = opcionElegida(lector);
			switch(respuesta) {
				case 1:
					agregarTarea(lector);
					visualizar = true;
					break;
				case 2:
					if(!tareas.isEmpty()) {
						modificarTarea(lector);
						visualizar = true;
					}else{
						limpiarPantalla();
						System.out.println("No hay tareas que modificar.\n");
						visualizarTareas();
						visualizarMenu();
					}
					break;
				case 3: 
					obtenerFraseMotivadora();
					break;
				case 4:
					if(!tareas.isEmpty()) {
						guardarTareas(lector);
					}else{
						limpiarPantalla();
						System.out.println("No hay tareas suficientes para guardar la sesi\u00f3n actual.\n");
						visualizarTareas();
						visualizarMenu();
					}
					break;
				case 5: 
					obtenerFraseMotivadora();
					continuar=false;
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
		String nombre;
		int estado;
		char repetir = 'N', basura;
		
		do {
			repetir = 'N';
			System.out.println("Introduce la ruta en la que tienes el fichero de la sesi\u00f3n anterior (incluido el nombre y extensi\u00f3n del fichero).");
			ruta = lector.readLine();
			
			sesionAntigua = new File(ruta);
			if(sesionAntigua.isFile()) {
				fis = new FileInputStream(sesionAntigua);
				dis = new DataInputStream(fis);
				
				// Leemos el fichero y agregamos las tareas al ArrayList
				while(dis.available() > 0) {
					estado = dis.readInt();
					nombre = dis.readUTF();
					basura = dis.readChar(); // debemos leer el salto de linea para evitar problemas
					
					tareas.add(new Tarea(estado, nombre));
				}
				limpiarPantalla();
				System.out.println("Sesi\u00f3n cargada correctamente.\n");
				visualizarTareas();
				visualizarMenu();
			}else {
				System.out.println("Ha ocurrido un error durante el proceso. ¿Quieres volver a repetirlo? SI o NO");
				repetir = lector.readLine().toUpperCase().charAt(0);
			}			
		}while(repetir != 'N');
	}
	
	public static void visualizarTareas() {
		System.out.println("Tus tareas actuales son: \n");
		if(!tareas.isEmpty()) {
			boolean todasCompletadas = true; // si estan todas completadas se mostrará un mensaje especial.
			for(Tarea tr : tareas) {
				if( todasCompletadas && tr.getEstado() == 0)
					todasCompletadas = false;
				
				System.out.println("\t [" + (tr.getEstado() == 0 ? porHacer : completado) + "] \t " + tr.getNombre() + "\n");
			}
			
			if(todasCompletadas) {
				System.out.println("\t Parece que has completado todas tus tareas, ¡ENHORABUENA! \n \t ¿A qu\u00e9 esperas para empezar tu siguiente desafio?");
			}
		}else {
			System.out.println("\t No hay tareas pendientes. Agrega una para empezar.");
		}
	}
	
	public static void visualizarMenu(){
		System.out.println("\n \t Men\u00fa de opciones: \n"
				+ "\t .......................................................... \n"
				+ "\t\t 1 - Agregar nueva tarea \n"
				+ "\t\t 2 - Modificar tarea existente \n"
				+ "\t\t 3 - Mostrar frase motivadora \n"
				+ "\t\t 4 - Guardar sesi\u00f3n actual \n"
				+ "\t\t 5 - Cerrar aplicaci\u00f3n \n");
	}
	
	public static int opcionElegida(BufferedReader lector) throws IOException {
		int respuesta=0;
		boolean error = true;
		
		System.out.print("¿Qu\u00e9 quieres hacer? ");
		do {
			try {
				respuesta = Integer.parseInt(lector.readLine());
				if(respuesta >= 1 && respuesta <= 4)
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
		
		limpiarPantalla();
		
		System.out.print("Introduce el nombre de la nueva tarea: ");
		nombre = lector.readLine();
		
		System.out.print("¿Quieres marcar la tarea como completada? SI o NO: ");
		// Si no introduce cualquier otra cosa que no sea un SI, damos por hecho que es que un NO.
		estado = lector.readLine().toUpperCase().charAt(0) == 'S' ? 1 : 0;
		
		tareas.add(new Tarea(estado, nombre));
		limpiarPantalla();
		System.out.println("Tarea nueva agregada.\n");
		visualizarTareas();
		visualizarMenu();
	}
	
	public static void modificarTarea(BufferedReader lector) throws IOException{
		String nuevoNombre;
		int respuesta=0, accion=0, index=0;
		boolean error=true;
		
		limpiarPantalla();
		
		System.out.println("Las tareas son: \n");
		
		for(Tarea tr : tareas) {
			System.out.println("\t" + (index+1) + ". [" + (tr.getEstado() == 0 ? porHacer : completado) + "] \t " + tr.getNombre() + "\n");
			index++;
		}
		
		System.out.print("Tarea a modificar: ");
		do {
			try {
				respuesta = Integer.parseInt(lector.readLine());
				if(respuesta >= 1 && respuesta <= tareas.size())
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
			tareas.get(respuesta).setNombre(nuevoNombre);
		}else if(accion == 2) {
			tareas.get(respuesta).setEstado(tareas.get(respuesta).getEstado() == 1 ? 0 : 1);
		}else if(accion == 3){
			System.out.print("Introduce el nuevo nombre para la tarea: ");
			nuevoNombre = lector.readLine();
			tareas.get(respuesta).setNombre(nuevoNombre);
			tareas.get(respuesta).setEstado(tareas.get(respuesta).getEstado() == 1 ? 0 : 1);
		}else{
			tareas.remove(respuesta);
		}
		limpiarPantalla();
		System.out.println("Tarea modificada.\n");
		visualizarTareas();
		visualizarMenu();
	}
	
	public static void obtenerFraseMotivadora() throws IOException {
		File flMotivacion = new File(ficheroFrasesMot);
		BufferedReader contarLineas;
		BufferedReader leerFrase;
		Random rd;
		int cntLineas, numAleatorio;
		String frase;
		
		limpiarPantalla();
		if(flMotivacion.isFile()) {
			// contamos la cantidad de lineas que tiene el fichero
			contarLineas = new BufferedReader(new FileReader(flMotivacion));
			cntLineas = 0;
			while(contarLineas.readLine() != null) {
				cntLineas++;
			}
			
			// declaramos el random para seleccionar una linea aleatoria del fichero
			rd = new Random();
			numAleatorio = rd.nextInt(0, cntLineas);
			
			// leemos la frase y la mostramos
			leerFrase = new BufferedReader(new FileReader(flMotivacion));
			limpiarPantalla();
			for(int i = 0; i<=numAleatorio;i++) {
				frase = leerFrase.readLine();
				if(i == numAleatorio)
					System.out.println(frase+"\n");
			}
		}else{
			System.out.println("Error al iniciar el fichero. ¡Pero tu puedes con todo! (o no)\n");
		}
		visualizarTareas();
		visualizarMenu();
	}
	
	public static void obtenerFraseMotivadora() throws IOException {
		File flMotivacion = new File(ficheroFrasesMot);
		BufferedReader contarLineas;
		BufferedReader leerFrase;
		Random rd;
		int cntLineas, numAleatorio;
		String frase;
		
		if(flMotivacion.isFile()) {
			// contamos la cantidad de lineas que tiene el fichero
			contarLineas = new BufferedReader(new FileReader(flMotivacion));
			cntLineas = 0;
			while(contarLineas.readLine() != null) {
				cntLineas++;
			}
			
			// declaramos el random para seleccionar una linea aleatoria del fichero
			rd = new Random();
			numAleatorio = rd.nextInt(0, cntLineas);
			
			// leemos la frase y la mostramos
			leerFrase = new BufferedReader(new FileReader(flMotivacion));
			for(int i = 0; i<=numAleatorio;i++) {
				frase = leerFrase.readLine();
				if(i == numAleatorio)
					System.out.println(frase);
			}
		}else {
			System.out.println("Error al iniciar el fichero. ¡Pero tu puedes con todo! (o no)");
		}
	}
	
	public static void guardarTareas(BufferedReader lector) throws IOException {
		File carpeta;
		File ficheroDestino;
		FileOutputStream fos;
		DataOutputStream dos;
		String ruta, nombre;
		
		System.out.println("Introduce la ruta en la que quieres guardar la sesi\u00f3n (s\u00f3lo la carpeta):");
		ruta = lector.readLine();
		carpeta = new File(ruta);
		
		if(carpeta.isDirectory()) {
			nombre = String.valueOf(System.currentTimeMillis()) + ".bin"; // nos devuelve la fecha actual en milisegundos, de esta forma nos aseguramos de que el nombre sea único
			ficheroDestino = new File(carpeta, nombre);
			fos = new FileOutputStream(ficheroDestino);
			dos = new DataOutputStream(fos);
			
			for(Tarea tr : tareas) {
				dos.writeInt(tr.getEstado());
				dos.writeUTF(tr.getNombre());
				dos.writeChar('\n'); // agregamos un salto de linea para luego poder leer la cantidad de tareas que tenemos (al cargarlo)
			}
			dos.close();
			fos.close();
			limpiarPantalla();
			System.out.println("Sesi\u00f3n guardada correctamente.\n");
			visualizarTareas();
			visualizarMenu();
		}else if(carpeta.isFile()) {
			System.out.println("Has introducido un fichero, proceso finalizado.");
		}else{
			System.out.println("La ruta introducida no existe.");
		}
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
