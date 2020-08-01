package mr.dht.util;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import mr.dht.peer2peernetwork.handlers.MessageHandler;
import mr.dht.peer2peernetwork.nodes.Setting;
import mr.resourcemanagement.datapartitioning.Partitioner;
import mr.resourcemanagement.execution.mrtasks.MRTask;

public class ClassLoader {
	public static URLClassLoader getClassLoader(String path) throws MalformedURLException{
		File f 				= new File(path);
		URL[] cp 			= {f.toURI().toURL()};
		return  new URLClassLoader(cp);
	}
	
	public static <K1,V1,K2,V2> Class<MRTask<K1,V1,K2,V2>> loadTask(String path, String cName) throws MalformedURLException, ClassNotFoundException{
		if (path == null || path.isEmpty())
			return  (Class<MRTask<K1,V1,K2,V2>>) Class.forName(cName);
		return (Class<MRTask<K1,V1,K2,V2>>) getClassLoader(path).loadClass(cName);
	}
	
	public static <K, V> Class<Partitioner<K,V>> loadPartitioner(String path, String cName) throws MalformedURLException, ClassNotFoundException{
		if (path == null || path.isEmpty())
			return  (Class<Partitioner<K, V>>) Class.forName(cName);
		return (Class<Partitioner<K, V>>) getClassLoader(path).loadClass(cName);
	}
	
	public static void loadingJarClass(String path) throws IOException, ClassNotFoundException{
		JarFile jarFile 		= new JarFile(path);
		Enumeration e 			= jarFile.entries();

		URL[] urls 			= { new URL("jar:file:" + path+"!/") };
		URLClassLoader cl = URLClassLoader.newInstance(urls);

		    while (e.hasMoreElements()) {
		        JarEntry je = (JarEntry) e.nextElement();
		        if(je.isDirectory() || !je.getName().endsWith(".class")){
		            continue;
		        }
		    // -6 because of .class
		    String className = je.getName().substring(0,je.getName().length()-6);
		    className = className.replace('/', '.');
		    Class c = cl.loadClass(className);
		}
	}
}
