package caelum.focalworks.api;

import at.petrak.hexcasting.api.casting.SpellList;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;

import java.util.HashMap;

public class RiggedHexFinder {

    public static final RiggedHexFinder INSTANCE = new RiggedHexFinder();
    private final HashMap<Class<?>, RiggedSupplier> finders;

    public RiggedHexFinder() {
        finders = new HashMap<>();
    }

    public SpellList getHex(Object object, CastingEnvironment env, String toRead) {

        Class<?> objectClass = object.getClass();

        if (!(finders.containsKey(objectClass))) {return null;}

        RiggedSupplier supplier = finders.get(objectClass);

        return supplier.getRiggedHex(object, env, toRead);
    }

    public void setHex(Object object, CastingEnvironment env, SpellList hex, String toRead) {
        Class<?> objectClass = object.getClass();

        if (!(finders.containsKey(objectClass))) {return;}

        RiggedSupplier supplier = finders.get(objectClass);

        supplier.setRiggedHex(object, env, hex, toRead);
    }

    public void setFinder(RiggedSupplier finder, Class<?> target) {
        finders.put(target,finder);
    }

    public RiggedSupplier getFinder(Class<?> target) {
        return finders.get(target);
    }
    // Do note you can do this anywhere! I'm just putting it in here because why not
    static {
        RiggedHexFinder INSTANCE = RiggedHexFinder.INSTANCE;
        
    }
}


