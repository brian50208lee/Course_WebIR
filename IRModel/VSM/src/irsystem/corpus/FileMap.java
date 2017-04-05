package irsystem.corpus;

import java.io.File;
import java.util.HashMap;


public class FileMap {
	private class Struct{
		public String id;
		public String path;
		public Integer index;
	}
	private HashMap<String, Struct> id_struct_map;
	private HashMap<String, Struct> path_struct_map;
	private HashMap<Integer, Struct> index_struct_map;
	private int size;
	
	public FileMap(){
		this.id_struct_map = new HashMap<String, Struct>();
		this.path_struct_map = new HashMap<String, Struct>();
		this.index_struct_map = new HashMap<Integer, Struct>();
		this.size = 0;
	}
	
	public int getSize() { return this.size;}

	public String id_to_path(String id) { return this.id_struct_map.get(id).path;}
	public Integer id_to_index(String id) { return this.id_struct_map.get(id).index;}

	public String path_to_id(String path) { return this.path_struct_map.get(path).id;}
	public Integer path_to_index(String path) { return this.path_struct_map.get(path).index;}
	
	public String index_to_id(int index) { return this.index_struct_map.get(index).id;}
	public String index_to_path(int index) { return this.index_struct_map.get(index).path;}
	
	public void addPath(String path){
		String path_tokens[] = path.split(File.separator);
		String id = path_tokens[path_tokens.length - 1];
		Struct struct = new Struct();
		
		struct.id = id;
		struct.path = path;
		struct.index = this.size;
		
		if (this.id_struct_map.containsKey(struct.id)) { return;}
		if (this.path_struct_map.containsKey(struct.path)) { return;}
		if (this.index_struct_map.containsKey(struct.index)) { return;}

		this.id_struct_map.put(struct.id, struct);
		this.path_struct_map.put(struct.path, struct);
		this.index_struct_map.put(struct.index, struct);
		this.size++;
	}
	
}
