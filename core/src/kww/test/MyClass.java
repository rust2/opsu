package kww.test;

import kww.test.SomeClass.Callback;

public class MyClass implements Callback {
    @Override
    public void callingBack()
    {
        System.out.println("Вызов метода обратного вызова");
    }
}

