/**
 * Generated by Gas3 v2.2.0 (Granite Data Services).
 *
 * WARNING: DO NOT CHANGE THIS FILE. IT MAY BE OVERWRITTEN EACH TIME YOU USE
 * THE GENERATOR. INSTEAD, EDIT THE INHERITED CLASS (PersonService.as).
 */

package org.granite.example.addressbook.spring.service {

    import flash.utils.flash_proxy;
    import org.granite.example.addressbook.entity.Person;
    import org.granite.tide.BaseContext;
    import org.granite.tide.Component;
    import org.granite.tide.ITideResponder;
    
    use namespace flash_proxy;

    public class PersonServiceBase extends Component {

        public function createPerson(arg0:Person, resultHandler:Object = null, faultHandler:Function = null):void {
            if (faultHandler != null)
                callProperty("createPerson", arg0, resultHandler, faultHandler);
            else if (resultHandler is Function || resultHandler is ITideResponder)
                callProperty("createPerson", arg0, resultHandler);
            else if (resultHandler == null)
                callProperty("createPerson", arg0);
            else
                throw new Error("Illegal argument to remote call (last argument should be Function or ITideResponder): " + resultHandler);
        }

        public function create100Persons(resultHandler:Object = null, faultHandler:Function = null):void {
            if (faultHandler != null)
                callProperty("create100Persons", resultHandler, faultHandler);
            else if (resultHandler is Function || resultHandler is ITideResponder)
                callProperty("create100Persons", resultHandler);
            else if (resultHandler == null)
                callProperty("create100Persons");
            else
                throw new Error("Illegal argument to remote call (last argument should be Function or ITideResponder): " + resultHandler);
        }

        public function modifyPerson(arg0:Person, resultHandler:Object = null, faultHandler:Function = null):void {
            if (faultHandler != null)
                callProperty("modifyPerson", arg0, resultHandler, faultHandler);
            else if (resultHandler is Function || resultHandler is ITideResponder)
                callProperty("modifyPerson", arg0, resultHandler);
            else if (resultHandler == null)
                callProperty("modifyPerson", arg0);
            else
                throw new Error("Illegal argument to remote call (last argument should be Function or ITideResponder): " + resultHandler);
        }

        public function deletePerson(arg0:Number, resultHandler:Object = null, faultHandler:Function = null):void {
            if (faultHandler != null)
                callProperty("deletePerson", arg0, resultHandler, faultHandler);
            else if (resultHandler is Function || resultHandler is ITideResponder)
                callProperty("deletePerson", arg0, resultHandler);
            else if (resultHandler == null)
                callProperty("deletePerson", arg0);
            else
                throw new Error("Illegal argument to remote call (last argument should be Function or ITideResponder): " + resultHandler);
        }

        public function findPersons(arg0:Person, arg1:int, arg2:int, arg3:Array, arg4:Array, resultHandler:Object = null, faultHandler:Function = null):void {
            if (faultHandler != null)
                callProperty("findPersons", arg0, arg1, arg2, arg3, arg4, resultHandler, faultHandler);
            else if (resultHandler is Function || resultHandler is ITideResponder)
                callProperty("findPersons", arg0, arg1, arg2, arg3, arg4, resultHandler);
            else if (resultHandler == null)
                callProperty("findPersons", arg0, arg1, arg2, arg3, arg4);
            else
                throw new Error("Illegal argument to remote call (last argument should be Function or ITideResponder): " + resultHandler);
        }
    }
}
