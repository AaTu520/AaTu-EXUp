package cn.blockforge.aatu_exup;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class AaTu_EXUp_ConfigScreen extends Screen {
    private static final int PAGE_SIZE = 7;

    private final Screen parent;
    private final List<AaTu_EXUp_ModConfig.EditableValue> entries;
    private final Map<String, String> pendingValues = new HashMap<>();
    private final List<EditBox> visibleFields = new ArrayList<>();
    private int page;

    public AaTu_EXUp_ConfigScreen(Screen parent) {
        super(Component.translatable("config.aatu_exup.title"));
        this.parent = parent;
        this.entries = AaTu_EXUp_ModConfig.editableValues();
        for (AaTu_EXUp_ModConfig.EditableValue entry : entries) {
            pendingValues.put(entry.key(), entry.value());
        }
    }

    @Override
    protected void init() {
        rebuildConfigWidgets();
    }

    private void rebuildConfigWidgets() {
        clearWidgets();
        visibleFields.clear();

        int formWidth = Math.min(440, width - 20);
        int formX = (width - formWidth) / 2;
        int fieldWidth = 150;
        int fieldX = formX + formWidth - fieldWidth;
        List<AaTu_EXUp_ModConfig.EditableValue> currentEntries = pageEntries();
        for (int index = 0; index < currentEntries.size(); index++) {
            AaTu_EXUp_ModConfig.EditableValue entry = currentEntries.get(index);
            EditBox field = new EditBox(font, fieldX, 55 + index * 21, fieldWidth, 20, labelFor(entry.key()));
            field.setMaxLength(128);
            field.setValue(pendingValues.getOrDefault(entry.key(), entry.value()));
            visibleFields.add(addRenderableWidget(field));
        }

        int buttonWidth = 72;
        int buttonGap = 4;
        int buttonX = (width - (buttonWidth * 4 + buttonGap * 3)) / 2;
        int buttonY = height - 28;
        addRenderableWidget(Button.builder(Component.translatable("config.aatu_exup.back"), button -> onClose())
                .bounds(buttonX, buttonY, buttonWidth, 20).build());
        addRenderableWidget(Button.builder(Component.translatable("config.aatu_exup.previous"), button -> changePage(-1))
                .bounds(buttonX + buttonWidth + buttonGap, buttonY, buttonWidth, 20).build());
        addRenderableWidget(Button.builder(Component.translatable("config.aatu_exup.next"), button -> changePage(1))
                .bounds(buttonX + (buttonWidth + buttonGap) * 2, buttonY, buttonWidth, 20).build());
        addRenderableWidget(Button.builder(Component.translatable("config.aatu_exup.save"), button -> saveChanges())
                .bounds(buttonX + (buttonWidth + buttonGap) * 3, buttonY, buttonWidth, 20).build());
    }

    private void changePage(int change) {
        rememberVisibleValues();
        int nextPage = Math.max(0, Math.min(pageCount() - 1, page + change));
        if (nextPage != page) {
            page = nextPage;
            rebuildConfigWidgets();
        }
    }

    private void saveChanges() {
        rememberVisibleValues();
        AaTu_EXUp_ModConfig.saveEditedValues(pendingValues);
        minecraft.setScreen(parent);
    }

    private void rememberVisibleValues() {
        List<AaTu_EXUp_ModConfig.EditableValue> currentEntries = pageEntries();
        for (int index = 0; index < currentEntries.size(); index++) {
            pendingValues.put(currentEntries.get(index).key(), visibleFields.get(index).getValue());
        }
    }

    private List<AaTu_EXUp_ModConfig.EditableValue> pageEntries() {
        int fromIndex = page * PAGE_SIZE;
        int toIndex = Math.min(entries.size(), fromIndex + PAGE_SIZE);
        return entries.subList(fromIndex, toIndex);
    }

    private int pageCount() {
        return Math.max(1, (entries.size() + PAGE_SIZE - 1) / PAGE_SIZE);
    }

    private Component labelFor(String key) {
        return Component.translatable("config.aatu_exup." + key);
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parent);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(font, title, width / 2, 15, 0xFFFFFF);
        guiGraphics.drawCenteredString(font,
                Component.translatable("config.aatu_exup.page", page + 1, pageCount()), width / 2, 35, 0xA0A0A0);

        int formWidth = Math.min(440, width - 20);
        int formX = (width - formWidth) / 2;
        List<AaTu_EXUp_ModConfig.EditableValue> currentEntries = pageEntries();
        for (int index = 0; index < currentEntries.size(); index++) {
            guiGraphics.drawString(font, labelFor(currentEntries.get(index).key()), formX, 61 + index * 21, 0xFFFFFF);
        }
        guiGraphics.drawCenteredString(font, Component.translatable("config.aatu_exup.note.local"), width / 2, height - 50, 0xA0A0A0);
    }
}