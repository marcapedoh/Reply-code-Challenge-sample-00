public class Program{
    public static void main(String[] args){

    }

    public static char to_hex(int i){
        return i<=9 ? (char) ('0'+i): (char)('A'+(i-10));
    }
    public static int from_hex(char c){
        if(c>='0' && c<='9'){
            return c-'0';
        }else{
            if(c>='A' && c<='F'){
                return 10+c-'A';
            }else{
                return -1;
            }
        }
    }
}