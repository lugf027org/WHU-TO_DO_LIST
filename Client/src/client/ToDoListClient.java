package client;

import CreatorModule.Creator;
import CreatorModule.CreatorHelper;
import UserModule.User;
import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Properties;

public class ToDoListClient {
    public static Creator creator;
    public static User user;
    public static BufferedReader reader;
    public static ORB orb;
    public static org.omg.CORBA.Object objRef;
    public static NamingContextExt ncRef;

    public ToDoListClient() {
        reader = new BufferedReader(new InputStreamReader(System.in));
    }

    public static void init() {
        System.out.println("Client init config starts....");
        String[] args = {};
        Properties properties = new Properties();
        properties.put("org.omg.CORBA.ORBInitialHost", "127.0.0.1");  //指定ORB的ip地址
        properties.put("org.omg.CORBA.ORBInitialPort", "1050");       //指定ORB的端口

        orb = ORB.init(args, properties);

        try {
            objRef = orb.resolve_initial_references("NameService");
        } catch (InvalidName e) {
            e.printStackTrace();
        }
        ncRef = NamingContextExtHelper.narrow(objRef);

        String name = "Creator";
        try {
            creator = CreatorHelper.narrow(ncRef.resolve_str(name));
        } catch (NotFound e) {
            e.printStackTrace();
        } catch (CannotProceed e) {
            e.printStackTrace();
        } catch (org.omg.CosNaming.NamingContextPackage.InvalidName e) {
            e.printStackTrace();
        }

        System.out.println("Client init config ends...");
    }
}
