package be.fabrice.lazyProperties.entity;

public interface LazyInitializable<T extends LazyProperties> {
	T getLazyProperties();
}
