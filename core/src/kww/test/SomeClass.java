package kww.test;

public class SomeClass {
    // создаем колбек и его метод
    interface Callback {
        void callingBack();
    }

    Callback callback;

    public void registerCallBack(Callback callback)
    {
        this.callback = callback;
    }

    void doSomething()
    {
        System.out.println("Выполняется работа");
        // вызываем метод обратного вызова
        callback.callingBack();
    }
}
