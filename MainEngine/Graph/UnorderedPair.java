package MainEngine.Graph;

/*
 * Classe représentant une paire non ordonnée, c'est-à-dire un couple de valeurs
 * Deux couples égaux sont par exemple (A, B) et (B, A)
 */
public class UnorderedPair {
	
	public UnorderedPair(String left , String right) {
		this.left = left;
		this.right = right;
	}
	
	public String getRight() {
		return right;
	}

	public String getLeft() {
		return left;
	}

	@Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) 
        	return false;  
        
        UnorderedPair rhs = (UnorderedPair) o; 
        
        return (left.equals(rhs.left) && right.equals(rhs.right)) || (left.equals(rhs.right) && right.equals(rhs.left)); 
    }
	
	@Override
	public int hashCode() {
		return 31 * 7 + left.hashCode() + right.hashCode();
	}
	
	private String right;
	private String left;
}
