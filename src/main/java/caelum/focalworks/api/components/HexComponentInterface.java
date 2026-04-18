package caelum.focalworks.api.components;

import at.petrak.hexcasting.api.casting.iota.Iota;
import dev.onyxstudios.cca.api.v3.component.Component;

public interface HexComponentInterface extends Component {
    Iota getHex();
    void setHex(Iota toSet);
}
