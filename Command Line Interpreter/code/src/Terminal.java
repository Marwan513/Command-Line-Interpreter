import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.io.*;
import java.lang.*;
import java.util.stream.Stream;


class Color {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_CYAN = "\u001B[36m";

    public String printWarning (String p){
        return (ANSI_YELLOW + p + ANSI_RESET);
    }
    public String print$ (String p) { return (ANSI_CYAN + p + ANSI_RESET); }
    public String printError (String p) { return (ANSI_RED + p + ANSI_RESET); }
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////

class Parser {
    String commandName;
    ArrayList<String> args = new ArrayList<String>();
    String[] in;
    String[] argsarr;

    public void emptyattr(){                //to empty cmd and args array after each command finished
        commandName = "";
        args.clear();
    }
    //This method will divide the input into commandName and args
    //where "input" is the string command entered by the user
    public boolean parse(String input) {
        if (!input.contains(" ")){
            commandName = input;
        }
        else {
            in = input.split(" ", 2);
            commandName = in[0];
            argsarr = in[1].split(" ");
            for(int i = 0;i<argsarr.length;++i){
                args.add(argsarr[i]);
            }
        }
        if (commandName.equalsIgnoreCase("data") || commandName.equalsIgnoreCase("echo") || commandName.equalsIgnoreCase("cd") || commandName.equalsIgnoreCase("pwd") || commandName.equalsIgnoreCase("ls") || commandName.equalsIgnoreCase("mkdir") || commandName.equalsIgnoreCase("rmdir") || commandName.equalsIgnoreCase("cp") || commandName.equalsIgnoreCase("rm") ||  commandName.equalsIgnoreCase("cat") ){return true;}
        else if(commandName.equalsIgnoreCase("exit")){return false;}
        else{return true;}
    }

    public String getCommandName(){
        return commandName;
    }
    public ArrayList<String> getArguments(){
        return args;
    }
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public class Terminal {
    Parser parser;
    private String defLoc = System.getProperty("user.home")+"\\";
    private String curLoc = System.getProperty("user.home")+"\\";    //default directory (current)
    Color obj = new Color();
    Terminal(){
        parser = new Parser();
    }
    public void setLoc (String newLoc){
        curLoc = newLoc;
    }
    public String getDefLoc(){return defLoc;}
    public String getCurLoc(){return curLoc;}
    //Implement each command in a method, for example:
    public void pwd(){
        System.out.println(obj.printWarning(curLoc));
    }
    ///////////////////////////////////////////
    public void date() {
        LocalDateTime myDateObj = LocalDateTime.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String formattedDate = myDateObj.format(myFormatObj);
        System.out.println(formattedDate);
    }
    /////////////////////////////////////////////////////////////////////////////////////////
    public void cd (String path){
        //checks whether the directory available or not then return it
        File file = new File(path);
        File fileSameDir = new File(curLoc+path);
        if (path.equals( "..")){
            int counter = 0;
            int index = curLoc.length();
            boolean found = false;
            for (int i = 0; i < curLoc.length(); i++) {
                if ((curLoc.charAt(i)=='\\' || curLoc.charAt(i) == '/')){
                    found = true;
                }
                else if (found){
                    index = i-1;
                    found = false;
                }
            }
            System.out.println("Directory changed to "+obj.print$(curLoc.substring(0,index+1)));
            curLoc = curLoc.substring(0,index+1);
        }
        else if (path.toLowerCase().equals("desktop")){
            System.out.println("Directory changed to " + obj.print$("Desktop"));
            curLoc = (System.getProperty("user.home") + "\\Desktop"+"\\");
        }
        else if  (fileSameDir.exists()){
            System.out.println("Directory changed to " + obj.print$(path));
            curLoc = (curLoc+path+'\\');
        } else if (file.exists()){
            System.out.println("Directory changed to " + obj.print$(path));
            curLoc = path+"\\";
        }else if (path.equals("~")){
            System.out.println("Directory changed to " + obj.print$(path));
            curLoc = System.getProperty("user.home")+'\\';
            curLoc = System.getProperty("user.home")+'\\';
        }
        else{
            System.out.println(obj.printError("Path may not be available use -mkdir- command to create directory"));
        }
    }
    /////////////////////////////////////////////////////////////////////////////////////////
    public void echo(String s){
        System.out.print(s+" ");
    }
    ////////////////////////////////////////////////////////////////////////////////////////
    public void ls(){
        try {
            File filesout = new File(curLoc);
            if (filesout.exists()) {
                ArrayList<String> arr = new ArrayList<String>(Arrays.asList(filesout.list()));
                Collections.sort(arr);
                if (arr.size() <= 0) {
                    System.out.println("Directory is empty");
                } else {
                    Collections.sort(arr);
                    for (String s : arr) {
                        System.out.println(s);
                    }
                }
            } else {
                obj.printError("Path is not available");
            }
        } catch (Exception e) {
            System.out.println("path Error");
        }
    }
    //////////////////////////////////////////////////////
    public void lsr(){
        try {
            File filesout = new File(curLoc);
            if (filesout.exists()) {
                ArrayList<String> arr = new ArrayList<String>(Arrays.asList(filesout.list()));
                Collections.sort(arr,Collections.reverseOrder());
                if (arr.size() <= 0) {
                    System.out.println("Directory is empty");
                } else {
                    Collections.sort(arr,Collections.reverseOrder());
                    for (String s : arr) {
                        System.out.println(s);
                    }
                }
            } else {
                obj.printError("Path is not available");
            }
        } catch (Exception e) {
            System.out.println("path Error");
        }
    }
    ////////////////////////////////////////////////////////////
    public void mkdir (String path){

        File file = new File(path);
        File file2 = new File(curLoc+path);

        if (file2.exists()){
            System.out.println(path + obj.printWarning(" is already exists"));
        } else if (file2.mkdir()){
            System.out.println(path + obj.print$(" Created Successfully"));
        } else if(file.exists()){
            System.out.println(path + obj.printWarning(" is already exists"));
        }else if(file.mkdir()){
            System.out.println(path + obj.print$( " Created Successfully"));
        }
        else{
            System.out.println( obj.printError("Error path may not be right"));
        }
    }
    ////////////////////////////////////////////////////////////////////////////
    public void rmdir(String path ) {
        if (path.equals("*")){
            String curLoc = this.getCurLoc();
            File dir = new File(curLoc);
            String[] items = dir.list();
            for(String item:items) {
                File path1 = new File(curLoc+"\\"+item);
                String[] paths = path1.list();
                if(paths!=null && paths.length == 0) {
                    path1.delete();
                }
            }
            System.out.println(obj.print$("Deleted Successfully"));
        }
        else{
            File file = new File(path);
            File fileSameDir = new File(curLoc+path);
            String [] f1 = file.list();
            String [] f2 = fileSameDir.list();

            if (fileSameDir.exists() ){
                if (f2.length<=0){
                    fileSameDir.delete();
                    System.out.println(path + obj.print$(" Deleted Successfully"));
                }else{
                    System.out.println(obj.printError("Directory is not empty"));
                }
            } else if (file.exists()){
                if (f1.length<=0){
                    file.delete();
                    System.out.println(path + obj.print$(" Deleted Successfully"));
                }else{
                    System.out.println(obj.printError("Directory is not empty"));
                }
            }else{
                System.out.println(obj.printError("Directory does not exist use ( mkdir "+ path + " ) to create"));
            }
        }
    }
    /////////////////////////////////////////////////////////////////////////////
    public Boolean cp(String sourcePath, String destinationPath ) throws IOException{

        String a="";
        File f =new File(sourcePath);
        File f1= new File (curLoc+sourcePath);
        Boolean help = true;
        if (f1.exists()) {
            a=curLoc+sourcePath;
        }
        else if (f.exists()) {
            a=sourcePath;
        }
        else {
            System.out.println(obj.printError("can't find source to copy"));
            return false;
        }

        FileReader myReader= new FileReader(a);
        File file1= new File(destinationPath);
        File file2= new File(curLoc+destinationPath);
        String b = "";
        if (file2.exists()) {
            b=curLoc+destinationPath;
        }
        else if (file1.exists()) {
            b=destinationPath;
        }
        else {
            try{
                if(file2.createNewFile()) {
                    b=curLoc+destinationPath;
                    help = false;
                }
            }catch (IOException e){}

            if(help){
                try{
                    if(file1.createNewFile()) {
                        b=destinationPath;
                    }
                }catch (IOException e ){
                    System.out.println("Error");
                    return false;
                }
            }
        }

        FileWriter myWriter= new FileWriter(b);

        Scanner read= new Scanner(myReader);
        String s;
        while (read.hasNextLine()) {
            s = read.nextLine();
            myWriter.write(s+"\n");
        }

        myReader.close();
        myWriter.close();
        read.close();
        return true;

    }
    //////////////////////////////////////////////////////////////////////////////
    public void copy(File s,File d) throws FileNotFoundException {
        if (s.isDirectory()) {
            if (!d.exists()) {
                d.mkdir();
            }
            for (String f : s.list()) {
                copy(new File(s, f), new File(d, f));
            }
        } else {
            try (
                    InputStream input = new FileInputStream(s);
                    OutputStream output = new FileOutputStream(d)
            ) {
                byte[] buf = new byte[1024];
                int length;
                while ((length = input.read(buf)) > 0) {
                    output.write(buf, 0, length);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public void cpr(String s, String d) throws IOException {
    File src = new File(curLoc+s);
    File dest = new File(curLoc+d+"\\"+s);
    //File newSrc = new File(curLoc+d+"\\"+s);
    if (!dest.exists()){dest.mkdir();}
    try{
        copy(src,dest);
        System.out.println(obj.print$("Done"));
    }catch (Exception e){
        System.out.println(e);
    }
}
    //////////////////////////////////////////////////////////////////////////////
    public void rm(String sourcePath) {
        File f= new File (sourcePath);
        File f1= new File (curLoc+sourcePath);
        if (f1.exists()) {
            if(f1.delete())
                System.out.println(obj.print$(("deleted")));
            else
                System.out.println(obj.printWarning("Not deleted"));
        }
        else if (f.exists()) {
            if(f.delete())
                System.out.println(obj.print$("deleted"));
            else
                System.out.println(obj.printWarning("Not deleted"));
        }
        else{
            System.out.println(obj.printError("File is not exists"));
        }

    }
    ////////////////////////////////////////////////////////////////////////////
    public ArrayList<String> cat(ArrayList<String> a) throws FileNotFoundException {
        ArrayList<String> c=new ArrayList<String>();
        String b;
        for (String i:a) {
            File f =new File(i);
            File f2=new File(curLoc+i);
            String m;
            if (f2.exists()) {
                m=curLoc+i;
            }
            else if (f.exists()) {
                m=i;
            }
            else {
                System.out.println(obj.printError("File "+i+" Not existed"));
                continue;
            }
            FileReader f1= new FileReader (m);
            Scanner s= new Scanner (f1);
            while (s.hasNextLine()) {
                b=s.nextLine();
                c.add(b);
            }
            s.close();
        }
        return c;
    }
    /////////////////////////////////////////////////////////////////////////////
    public void touch(String path) throws IOException {
        File file = new File(path);
        File file2 = new File(curLoc + path);
        if(file.isAbsolute()){
            if (file.exists()) {
                System.out.println(path + obj.printWarning(" is already exists"));
            }
            else if (file.createNewFile()) {
                System.out.println(path + obj.print$(" Created Successfully"));
            }
        }
        else{
            if (file2.exists()) {
                System.out.println(path + obj.printWarning(" is already exists"));
            } else if (file2.createNewFile()) {
                System.out.println(path + obj.print$(" Created Successfully"));
            }
        }
    }
    /////////////////////////////////////////////////////////////////////////////
    //This method will choose the suitable command method to be called
    public void chooseCommandAction() throws IOException {
        Color color = new Color();

        if ( this.parser.getCommandName().equalsIgnoreCase("date") && this.parser.getArguments().size() == 0 ){
            this.date();
        }
        else if(this.parser.getCommandName().equalsIgnoreCase("echo") && this.parser.getArguments().size() != 0){
            for (String s : this.parser.getArguments()) {
                this.echo(s);
            }
            System.out.println();
        }
        else if(this.parser.getCommandName().equalsIgnoreCase("pwd") && this.parser.getArguments().size() == 0){
            this.pwd();
        }
        else if(this.parser.getCommandName().equalsIgnoreCase("cd") && ((this.parser.getArguments().size() == 0 ) || (this.parser.getArguments().size() == 1))){
            if(this.parser.getArguments().size() == 0){this.cd(getDefLoc());}
            else {
                this.cd(this.parser.getArguments().get(0));
            }
        }
        else if(this.parser.getCommandName().equalsIgnoreCase("ls") && (this.parser.getArguments().size() == 0 || (this.parser.getArguments().size() == 1 && this.parser.getArguments().get(0).equals("-r")))){
            if(this.parser.getArguments().size() == 0){
                this.ls();
            }
            else if(this.parser.getArguments().get(0).equals("-r")){
                this.lsr();
            }
        }
        else if(this.parser.getCommandName().equalsIgnoreCase("mkdir") && this.parser.getArguments().size() > 0){
            for (int i = 0;i<this.parser.getArguments().size();++i){
            this.mkdir(this.parser.getArguments().get(i));
            }
        }
        else if(this.parser.getCommandName().equalsIgnoreCase("rmdir") && this.parser.getArguments().size() == 1){
            this.rmdir(this.parser.getArguments().get(0));
        }
        else if(this.parser.getCommandName().equalsIgnoreCase("touch") && this.parser.getArguments().size() == 1){
            this.touch(this.parser.getArguments().get(0));
        }
        else if(this.parser.getCommandName().equalsIgnoreCase("cp") && this.parser.getArguments().size() == 2){
            this.cp(this.parser.getArguments().get(0),this.parser.getArguments().get(1));
        }
        else if(this.parser.getCommandName().equalsIgnoreCase("cp") && (this.parser.getArguments().size() == 3 && this.parser.getArguments().get(0).equals("-r"))){
            this.cpr(this.parser.getArguments().get(1),this.parser.getArguments().get(2));
        }
        else if(this.parser.getCommandName().equalsIgnoreCase("rm") && this.parser.getArguments().size() == 1){
            this.rm(this.parser.getArguments().get(0));
        }
        else if(this.parser.getCommandName().equalsIgnoreCase("cat") && (this.parser.getArguments().size() == 1 || this.parser.getArguments().size() == 2)){
            for (String i : this.cat(this.parser.getArguments())) {
                System.out.println(i);
            }
        }
        else {
            System.out.println(color.printError("this command is not recognized as an internal or external command"));
        }
        this.parser.emptyattr();

    }
    public static void main(String[] args) throws IOException {
        Terminal terminal = new Terminal();
        Scanner In = new Scanner(System.in);
        Color color = new Color();

        boolean exit = false;
        String input ;
        while (true){
            System.out.print(color.printWarning("user@OS:") + color.print$("~$ "));
            input = In.nextLine();
            terminal.parser.parse(input);
            exit = terminal.parser.getCommandName().equalsIgnoreCase("exit") ;
            if (exit){break;}
            terminal.chooseCommandAction();
        }

    }
}
