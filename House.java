import java.util.ArrayList;

public class House {
    private int x;
    private int y;
    private Type type; // type
    private State state; // state
    private ArrayList<House> neighbors; // 빈집들은 이웃으로 추가할 필요가 없으니 가변적으로 크기를 조절할 수 있게 arraylist을 이용하자. 
    private int[][] steps = { //neighbor을 체크할 방향의 스탭
        {-1,-1},{0,-1},{1,-1},
        {-1,0},{1,0},
        {-1,1},{0,1},{1,1}
    };
    private Segregation segregation; 

    // constructor
    //각 house들은 본인의 좌표값과 본인의 type을 생성할때 받는다.
    public House(int x, int y, Type type, Segregation segregation) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.segregation = segregation;
    }

    public ArrayList<House> getNeighbors() { 
        neighbors = new ArrayList<House>(); 
        for(int[] step:steps) {
            int[] grid = validLocation(this.x-step[0], this.y-step[1]);
            if (!this.getSegregation().getCity()[grid[0]][grid[1]].isEmpty()) //각 이웃의 좌표중에서 집이 비어있지 않다면 이웃으로 추가해 준다.
                neighbors.add(this.getSegregation().getCity()[grid[0]][grid[1]]);
        }
        return neighbors;
    }

    private int[] validLocation(int x, int y) { //도넛 모양으로 city가 구성되어 있으니 화면의 평면상에서 벗어난 영역을 유효한 영역으로 반환해 준다.
        int[] grid = {x,y};
        if (x < 0) {
            grid[0] = Segregation.getSize()-1;
        } else if (x == Segregation.getSize()) grid[0] = 0;
        if (y < 0) {
            grid[1] = Segregation.getSize()-1;
        } else if (y == Segregation.getSize()) grid[1] = 0;
        return grid;
    }

    // update state
    // unhappy if # of neighbors is 0 or
    // # of alike < # of neighbors * tolerance
    // unhappy if diversity is true and
    // all the neighbors(>1) are like this
    public void updateState(double tolerance, boolean diversity) {
        neighbors = getNeighbors();
        if(this.type == Type.EMPTY) this.state = State.NONE; //빈집이면 우선적으로 State가 None으로 해준다.
        else {
            if(!diversity) { //diversity의 boolean값에 따라 diversity의 조건을 추가해 준다.
                if(neighbors.size() == 0
                    || this.countNumOfAlikeNeighbors() < neighbors.size()*tolerance) this.state = State.UNHAPPY;
                else this.state = State.HAPPY;
            }else {
                if((this.countNumOfAlikeNeighbors() == neighbors.size() && neighbors.size() > 1) //diversity의 조건
                    || neighbors.size() == 0
                    || this.countNumOfAlikeNeighbors() < neighbors.size()*tolerance) this.state = State.UNHAPPY;
                else this.state = State.HAPPY;
            }
        }
    }

    //자신과 동일한 type을 가진 neighbor의 수를 반환해 준다.
    public int countNumOfAlikeNeighbors() {
        int count = 0;
        for(House neighbor: neighbors) {
            if(neighbor.getType().equals(this.getType())) count++;
        }
        return count;
    }
    // move to a new place
    public void moveTo(House other) { //state도 함깨 변경해줘야한다.
        other.type = this.type;
        this.type = Type.EMPTY;
    } 
        // return type
    public Type getType() {
        return this.type;
    }
    
    public Segregation getSegregation(){
        return this.segregation;
    }

    public boolean isEmpty() {
        return this.type == Type.EMPTY;
    }
    // return state
    public State getState() {
        return this.state;
    } 
    public boolean isHappy() {
        return this.state.equals(State.HAPPY);
    } 
    public boolean isUnhappy() {
        return this.state.equals(State.UNHAPPY);
    }
    // return true if all neighbors are like this
    public boolean isSegregated() {
        return this.countNumOfAlikeNeighbors() == neighbors.size();
    }
    // return string representation // HEAD 0, TAIL #
    public String toString() {
        Type t = this.type;
        return t.getIcon();
    }
}