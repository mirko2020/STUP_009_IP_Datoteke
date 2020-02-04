package yi.etf.studiranje.datoteke.bean;

/**
 * Опште зрно које се користи за страничење листе података.
 * @author Computer
 * @version 1.0
 */
public class ListPaggingBean {
	private long pageNo = 0; 
	private long pageSize = 10; 
	private long count = 0; 
	private long pageCount = 0; 
	
	public long getPageNo() {
		return pageNo; 
	}
	
	public long getPageSize() {
		return pageSize; 
	}
	
	public long getCount() {
		return count; 
	}
	
	public long getPageCount() {
		return pageCount;
	}
	
	public void setPageNo(long pageNo) {
		if(pageNo<0) pageNo = 0; 
		if(pageNo>pageCount) pageNo = pageCount; 
		this.pageNo = pageNo; 
	}
	
	public void setCount(long count) {
		if(count<0) count = 0; 
		this.count = count;
		if(count==0) pageCount=0; 
		else pageCount = count/pageSize+(count%pageSize==0?0:1); 
		if(pageNo>pageCount) pageNo = pageCount; 
	}
	
	public void setPageSize(long pageSize) {
		if(pageSize<1) pageSize = 1; 
		if(count==0) pageCount=0; 
		else pageCount = count/pageSize+(count%pageSize==0?0:1); 
		if(pageNo>pageCount) pageNo = pageCount; 
		this.pageSize = pageSize; 
	}
	
	public void setPageCount(long pageCount) {
		if(pageCount<1) pageCount=1;  
		if(pageCount>count) pageCount=count; 
		if(pageCount==0) pageSize = 1; 
		else pageSize = count/pageCount;
		if(pageSize==0) pageSize = 1; 
	}
	
	 public ListPaggingBean next() {
		 setPageNo(pageNo+1); 
		 return this; 
	 }
	 
	 public ListPaggingBean previous() {
		 setPageNo(pageNo-1); 
		 return this; 
	 }
}
