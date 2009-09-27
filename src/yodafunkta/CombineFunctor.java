package yodafunkta;

class CombineFunctor extends Functor {

    private final Functor first;
    private final Functor second;

    public CombineFunctor(Functor first, Functor second) {
        this.first = first;
        this.second = second;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T> T run(Object... parameters) {
        return (T) first.run(second.run(parameters));
    }

    @Override
    protected Functor cloneItself() {
        return new CombineFunctor(first, second);
    }
}
