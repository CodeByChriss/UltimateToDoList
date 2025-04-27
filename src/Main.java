import java.io.*;

public class Main {

    private static Tarea []tareas = new Tarea[0];

    /*
     * Array que va a contener todas las tareas de esta sesión.
     * Declaro el array a 0 ya que existe la posibilidad de que
     * si no se carga sesión, en el método visualizarTareas() 
     * se intente leer el array sin declarar, lo que causaría
     * un error.
     */
    public static void main(String[]args) throws IOException {
        BufferedReader lector = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("BIENVENIDO A ULTIMATE TO-DO LIST");

        System.out.println(" _    _ _ _   _                 _    _______    _____        _      _     _   ");
        System.out.println("| |  | | | | (_)               | |  |__   __|  |  __ \\      | |    (_)   | |  ");
        System.out.println("| |  | | | |_ _ _ __ ___   __ _| |_ ___| | ___ | |  | | ___ | |     _ ___| |_ ");
        System.out.println("| |  | | | __| | '_ ` _ \\ / _` | __/ _ \\ |/ _ \\| |  | |/ _ \\| |    | / __| __|");
        System.out.println("| |__| | | |_| | | | | | | (_| | ||  __/ | (_) | |__| | (_) | |____| \\__ \\ |_ ");
        System.out.println(" \\____/|_|\\__|_|_| |_| |_|\\__,_|\\__\\___|_|\\___/|_____/ \\___/|______|_|___/\\__|\n");

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
}
