package com.xtonic.exception;

public class GetTotalCountException extends Exception {
	
	private static final long serialVersionUID = 2146723536917271795L;

		public GetTotalCountException(String msg ,Exception e ) {
			super(msg,e);
		}
}
