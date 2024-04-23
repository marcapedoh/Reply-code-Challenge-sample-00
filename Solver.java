import java.util.*;

public class Solver{
    int w,h;
    List<Point> goldens;
    List<Point> silvers;
    Tile[] tiles;
    List<List<Character>> grid;
    List<List<Character>> init_grid;

    public static int read_type(){
        char c;
        Scanner sc= new Scanner(System.in);
        return Program.from_hex(sc.next().charAt(0));
    }

    public void read(){
        int gn,sn,tn;
        Scanner sc= new Scanner(System.in);
        w=sc.nextInt();
        h=sc.nextInt();
        gn=sc.nextInt();
        sn=sc.nextInt();
        tn=sc.nextInt();
        for(int i=0;i<gn;i++){
            int x,y;
            x=sc.nextInt();
            y=sc.nextInt();
            goldens.add(new Point(x, y, 0));
        }
        for(int i=0;i<sn;i++){
            int x,y,c; 
            x=sc.nextInt();
            y=sc.nextInt();
            c=sc.nextInt();
            goldens.add(new Point(x, y, c));
        }
        tiles= new Tile[16];

        for(int i=0;i<tn;i++){
            int type= read_type();
            int count, cost;
            count=sc.nextInt();
            cost=sc.nextInt();
            tiles[type]= new Tile(count, cost);
        }
    }
    public void draw(){
        grid= new ArrayList<>(Collections.nCopies(h,new ArrayList<>(h)));
        for(int i=0; i<h;i++){
            List<Character> row= new ArrayList<>();
            for(int j=0;j<w; j++){
                row.add('.');
            }
            grid.add(row);
        }
        for(Point point: silvers){
            grid.get(point.x).set(point.y,'S');
        }
        for(Point point:goldens){
            grid.get(point.x).set(point.y,'G');
        }
        init_grid=grid;
    }
    public void solve(){
        List<List<Boolean>> visit= new ArrayList<>(h);
        for(int i=0;i<h;i++){
            visit.add(new ArrayList<>(Collections.nCopies(w,false)));
        }
        double ratio=0.0;
        for(int i=0;i<h;i++){
            for(int j=0;j<w;j++){
                if(init_grid.get(i).get(j)=='G'){
                    visit.get(i).set(j,true);
                }else{
                    if(init_grid.get(i).get(j)=='S'){
                        visit.get(i).set(j,(j>=(int) (ratio*w) && j< (int) ((1-ratio)*w) && i>= (int) (ratio*h) && i<= (int) ((1-ratio) *h)));
                    }
                }
            }
        }
        for(int w=10;w<=10;w++){
            Random rng = new Random(58);
            grid=init_grid;
            Comparator<Point> comparator= new Comparator<Point>() {
                @Override
                public int compare(Point o1, Point o2) {
                    return Integer.compare(o1.x,o2.x);
                }
            };
            Collections.sort(goldens,comparator);
            boolean[] skip = new boolean[w];
            int gn = (int) (goldens.size());
            int half= gn/2;
            for(int i=0;i<gn;i++){
                var g=goldens.get(i);
                skip[g.x]=true;
                if(i<half-1){
                    skip[g.x+1]=true;
                }
                if(i>half){
                    skip[g.x-1]=true;
                }
            }
            skip[0]=skip[w-1]=true;
            List<Map.Entry<Integer,Integer>> stripes= new ArrayList<>();
            int beg=0;
            while(beg<w){
                if(skip[beg]){
                    beg+=1;
                    continue;
                }
                int end=beg;
                while(end+1<w && !skip[end+1]){
                    end+=1;
                }
                int width=end-beg+1;
                double best_diff=1e10;
                int best_cnt=-1;
                for(int cnt=2;cnt<=width;cnt+=2){
                    var avg= (double) (width)/cnt;
                    var diff= Math.abs(avg-w);
                    if(diff<best_diff){
                        best_diff=diff;
                        best_cnt=cnt;
                    }
                }
                List<Integer> cuts= new ArrayList<>();
                for(int i=0; i<=best_cnt;i++){
                    cuts.add(width*i/best_cnt);
                }
                for(int i=0; i<best_cnt;i++){
                    stripes.add(new AbstractMap.SimpleEntry<>(beg+cuts.get(i),beg+cuts.get(i+1)));
                }
                beg=end+1;
            }
            List<Integer> go_to= new ArrayList<>(Collections.nCopies(w,-1));
            for(Map.Entry<Integer,Integer> stripe: stripes){
                int key= stripe.getKey();
                int value= stripe.getValue();
                go_to.set(key,value);
            }
            int i=1,j=1,j1=j;
            while(j1<w-3){
                int j2=go_to.get(j1);
                assert j2!=-1;
                if(i<=2){
                    while (i<h-2){
                        int to=-1;
                        for(int c=j+1;c<j2;c++){
                            if(visit.get(i).get(c)){
                                to=c;
                            }
                        }
                        if(to!=-1){
                            grid.get(i).set(j,'9');
                            for(int c=j+1; c<to; c++){
                                grid.get(i).set(c,'3');
                            }
                            grid.get(i).set(to,'6');
                            j=to;
                            i+=1;
                            continue;
                        }
                        to=-1;
                        for(int c=j-1;c>j1;c--){
                            if(visit.get(i).get(c)){
                                to=c;
                            }
                        }
                        if(to!=-1){
                            grid.get(i).set(j,'A');
                            for(int c= j-1;c>to;c--){
                                grid.get(i).set(c,'3');
                            }
                            grid.get(i).set(to,'5');
                            j=to;
                            i+=1;
                            continue;
                        }
                        grid.get(i).set(j,'C');
                        i+=1;
                    }
                    grid.get(i).set(j,'9');
                    j+=1;
                    while(j<j2 || (j<w-1 && skip[j])){
                        grid.get(i).set(j,'3');
                        j+=1;
                    }
                    grid.get(i).set(j,'A');
                    i-=1;
                }else{
                    while (i>=1){
                        int to= -1;
                        for(int c=j+1;c<j2;c++){
                            if(visit.get(i).get(c)){
                                to=c;
                            }
                        }
                        if(to!=-1){
                            grid.get(i).set(j,'5');
                            for(int c=j+1;c<to;c++){
                                grid.get(i).set(c,'3');
                            }
                            grid.get(i).set(to,'A');
                            j=to;
                            i-=1;
                            continue;
                        }
                        to=-1;
                        for(int c=j-1;c>=j1;c--){
                            if(visit.get(i).get(c)){
                                to=c;
                            }
                        }
                        if(to!=-1){
                            grid.get(i).set(j,'6');
                            for(int c=j-1;c>to;c--){
                                grid.get(i).set(c,'3');
                            }
                            grid.get(i).set(to,'9');
                            j=to;
                            i-=1;
                            continue;
                        }
                        grid.get(i).set(j,'C');
                        i-=1;
                    }
                    grid.get(i).set(j,'5');
                    j+=1;
                    while (j<j2|| (j<w-1 && skip[j])){
                        grid.get(i).set(j,'3');
                        j+=1;
                    }
                    grid.get(i).set(j,'6');
                    i+=1;
                }
                j1=j;
            }
            for(int I=half-1;I>=0;I--){
                var g=goldens.get(I);
                for(int r=g.y+1;r<h-1;r++){
                    grid.get(r).set(g.x,'C');
                }
                grid.get(h-1).set(g.x,'A');
                int next_c= I>0?goldens.get(I-1).x+1:0;
                for(int c=g.x-1;c>next_c;c--){
                    grid.get(h-1).set(c,'3');
                }
                grid.get(h-1).set(next_c,'9');
                int next_r= I>0?goldens.get(I-1).y:0;
                for(int r=h-2;r>next_r;r--){
                    grid.get(r).set(next_c,'C');
                }
                if(i>0){
                    grid.get(next_r).set(next_c,'6');
                }else{
                    grid.get(next_r).set(next_c,'5');
                    grid.get(next_r).set(next_c+1,'6');
                }
            }
            for(int k=half;k<gn;k++){
                var g=goldens.get(i);
                for(int r=g.y+1;r<h-1;r++){
                    grid.get(r).set(g.x,'C');
                }
                grid.get(h-1).set(g.x,'9');
                int next_c= k<gn-1?goldens.get(k+1).x-1:w-1;
                for(int c=g.x+1;c<next_c;c++){
                    grid.get(h-1).set(c,'3');
                }
                grid.get(h-1).set(next_c,'A');
                int next_r= k<gn-1?goldens.get(k+1).y:0;
                for(int r=h-2;r>next_r;r--){
                    grid.get(r).set(next_c,'C');
                }
                if(i<gn-1){
                    grid.get(next_r).set(next_c,'5');
                }else{
                    grid.get(next_r).set(next_c,'6');
                }
            }
            Map<Character,Integer> usage= new HashMap<>();
            for(int q=0;q<h;q++){
                for(int x=0;x<w;x++){
                    Character key = grid.get(q).get(x);
                    Integer value = usage.get(key);
                    usage.put(key,value+1);
                }
            }
            int total_cost=0;
            for(int z=0;z<16;z++){
                if(tiles[z].cost>0){
                    /*if(tiles[z].count<usage[Program.to_hex(z)])*/
                    total_cost+=tiles[z].cost*usage.get(Program.to_hex(i));
                }
            }
            for(int n=0;n<h;n++){
                for(int m=0;m<w;m++){
                    if(init_grid.get(n).get(m)=='G'){
                        grid.get(n).set(m,'G');
                    }
                }
            }
            for(int b=0;b<h;b++){
                for(int d=0;d<w;d++){
                    if(init_grid.get(b).get(d)!='G'){
                        char c= grid.get(b).get(d);
                        int x=Program.from_hex(c);
                    }
                }
            }
        }
    }


    public static void main(String[] args){
        Solver solver= new Solver();
        solver.read();
        solver.draw();
        solver.solve();
    }
}