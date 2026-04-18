package caelum.focalworks.api;

import at.petrak.hexcasting.api.casting.SpellList;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;

public interface RiggedSupplier {
    public SpellList getRiggedHex(Object target, CastingEnvironment env, String toRead);
    public void setRiggedHex(Object target, CastingEnvironment env, SpellList hex, String toSet);
}