/**
 * Generated by Gas3 v1.1.0 (Granite Data Services) on Sat Jul 26 17:58:20 CEST 2008.
 *
 * WARNING: DO NOT CHANGE THIS FILE. IT MAY BE OVERRIDDEN EACH TIME YOU USE
 * THE GENERATOR. CHANGE INSTEAD THE INHERITED CLASS (Contact.as).
 */

package org.granite.test.tide.framework {

	import org.granite.tide.Context;
	import org.granite.test.tide.Contact;
	

	[Name("myComponentInject")]
    public class MyComponentInjectGDS477 {
    	    	
    	private var _context:Context;
    	
		[In] [Bindable]
		public function set context(context:Context):void {
			_context = context;
		}
		
		public function get context():Context {
			return _context;
		}
    }
}