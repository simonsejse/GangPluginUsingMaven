package dk.simonwinther.utility;

public interface TriConsumer<K,V,Y>
{
    void accept(K k,V v,Y y);
}
