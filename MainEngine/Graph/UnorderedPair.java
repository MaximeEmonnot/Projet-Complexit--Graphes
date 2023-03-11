package MainEngine.Graph;

import java.util.Objects;

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
        
        UnorderedPair unorderedPair = (UnorderedPair) o; 
        
        return Objects.equals(getLeft(), unorderedPair.getLeft()) && Objects.equals(getLeft(), unorderedPair.getLeft()) 
        		|| Objects.equals(getLeft(), unorderedPair.getRight()) && Objects.equals(getRight(), unorderedPair.getLeft()); 
    }
	
	@Override
	public int hashCode() {
	    int hash = 7;
	    hash = 31 * hash + left.hashCode() + right.hashCode();
	    return hash;
	}
	
	private String right;
	private String left;
}
