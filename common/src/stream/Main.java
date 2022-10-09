package stream;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class  Main {

    static int foo(int a,int b){
        return a+b;
    }
    static int sum(int x, int y,Func func){
        return func.apply(x,y);
    }

    static void printString(Consumer<String> consumer, String value){
        consumer.accept(value);
    }

    public static void main(String[] args) {
        Func sum=(a,b)->a+b;
        System.out.println(sum.apply(1,2));
        Func sumRef=Main::foo;
        System.out.println(sumRef.apply(1,2));


        Calk calk=Main::sum;
        System.out.println(calk.calc(1,2,sum));
//action
        Consumer<String> printer=System.out::println;
        printer.accept("Hello world");
        Consumer<Integer> consumer=value->System.out.println(value);

        //for filtering
        Predicate<Integer> idOdd= value->value%2==1;
        System.out.println(idOdd.test(2));

        //for transform
        Function<String,Integer> toInt=Integer::parseInt;
        System.out.println(toInt.apply("1234"));

        Supplier<List<String>> emplyList= ArrayList::new;
        emplyList.get();

    }
}
