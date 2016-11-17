package it.polito.dp2.NFFG.sol1;

public interface Transformer<T extends Object, R extends Object> {
	public R transform();
}
