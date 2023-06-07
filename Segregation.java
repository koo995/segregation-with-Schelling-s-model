import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Segregation {
    //3개의 변수 모두 각 segregation객체마다 하나의 같은 변수를 사용하게 되니 static을 처리해 준다.
    private House[][] city; 
    private static int size = 30;
    private static double emptyRate = 0.25;
    
    // construct a city of size x size houses
    // each house is empty w.p emptyRate
    // each occupied house is HEADS w.p 0.5 and TAILS w.p 0.5 
    public Segregation(int size, double emptyRate) {
        city = new House[size][size];
        for(int i = 0; i < size; i++) {
            for(int j = 0; j<size; j++) {
                if (Math.random() < emptyRate) {
                    city[i][j] = new House(i, j, Type.EMPTY, this);
                }
                else {
                    if (Math.random() < 0.5) {
                        city[i][j] = new House(i,j,Type.HEADS, this);	
                    }
                    else {
                        city[i][j] = new House(i,j,Type.TAILS, this);
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        double tolerance = 0.3;
        boolean diversity = true;
        Segregation s = new Segregation(size, emptyRate);
        s.updateState(tolerance, diversity);
        s.printStep(0);

        for (int n = 0; n < 200; n++) {
            s.move();
            s.updateState(tolerance, diversity);
            s.printStep(n+1);
            System.out.println("\n");
            int[] counts = s.countStates();
            if (counts[State.UNHAPPY.ordinal()] == 0) break;
        }

    }
        // update the state of each house
    public void updateState(double tolerance, boolean diversity) {
        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                city[i][j].updateState(tolerance, diversity);
            }
        }
    }
    
    public void move() { //house하나하나에 대해서 확인하며 unhappy한 경우에 빈곳으로 옮긴다.
        for(int i = 0; i < size; i++) {
            for(int j = 0; j< size; j++) {
                if(city[i][j].isUnhappy()) {
                    city[i][j].moveTo(chooseEmptyHouse());  //여기서 두번의 이동이 발생가능하다.
                }
            }
        }
    }

    // 전체 city에서 빈집에 해당하는 집들을 따로 담아주고 랜덤으로 하나의 집을 반환한다.
    public House chooseEmptyHouse() {
        ArrayList<House> emptyHouses = new ArrayList<>();
        Random rand = new Random();
        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                if(city[i][j].isEmpty()) emptyHouses.add(city[i][j]);
            }
        }
        House emptyHouse = emptyHouses.get(rand.nextInt(emptyHouses.size()));
        return emptyHouse;
    }

    // return an array of type counts // [HEADS, TAILS, EMPTY]
    public int[] countTypes() {
        int[] count = {0,0,0};
        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                if (city[i][j].isEmpty()) count[2]++;
                else if(city[i][j].getType() == Type.TAILS) count[1]++;
                else if(city[i][j].getType() == Type.HEADS) count[0]++;
            }
        }
        return count;
    }
    // return an array of state counts // [NONE, UNHAPPY, HAPPY]
    public int[] countStates() {
        int[] count = {0,0,0};
        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                if (city[i][j].getState() == State.NONE) count[0]++;
                else if(city[i][j].isUnhappy()) count[1]++;
                else if(city[i][j].isHappy()) count[2]++;
            }
        }
        return count;
    }
    
    // return the number of segregated houses
    public int countSegregated() {
        int count = 0;
        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                if (city[i][j].isSegregated()) count++;
            }
        }
        return count;
    }
    
    // print the types of houses: size x size
    public void printCity() {
        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                System.out.print(city[i][j]);
            }
            System.out.println("\n");
        }
    }
    //3개의 method들은 private으로 선언한 변수들을 얻기위해 getter메소드로 만들어 두었다.
    public static int getSize() {
        return size;
    }
    public House[][] getCity() { 
        return city;
    }
    public static double getEmptyRate() {
        return emptyRate;
    }

    // print the statistics and the types of houses // step (empty / unhappy / happy / segregated) 
    public void printStep(int n) {
        System.out.println("-----------" + n +"------------");
        System.out.println("head tail empty: " + Arrays.toString(countTypes()));
        System.out.println("None UNHAPPY HAPPY: " + Arrays.toString(countStates()));
        System.out.println("segregated: " + countSegregated());
        printCity();
    }
}