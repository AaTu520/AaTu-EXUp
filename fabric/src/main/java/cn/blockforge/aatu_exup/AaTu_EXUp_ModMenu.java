package cn.blockforge.aatu_exup;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public final class AaTu_EXUp_ModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return AaTu_EXUp_ConfigScreen::new;
    }
}
