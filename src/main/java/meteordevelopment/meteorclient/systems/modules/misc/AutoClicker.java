/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2021 Meteor Development.
 */

package meteordevelopment.meteorclient.systems.modules.misc;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.mixininterface.IMinecraftClient;
import meteordevelopment.meteorclient.mixin.MinecraftClientAccessor;
import meteordevelopment.orbit.EventHandler;

public class AutoClicker extends Module {
    public enum Mode {
        Hold,
        Press,
        Manual
    }

    public enum Button {
        Right,
        Left
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
            .name("mode")
            .description("The method of clicking.")
            .defaultValue(Mode.Press)
            .build()
    );

    private final Setting<Button> button = sgGeneral.add(new EnumSetting.Builder<Button>()
            .name("button")
            .description("Which button to press.")
            .defaultValue(Button.Right)
            .build()
    );

    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
            .name("click-delay")
            .description("The amount of delay between clicks in ticks.")
            .defaultValue(2)
            .min(0)
            .sliderMax(60)
            .build()
    );

    private int timer;
    private MinecraftClientAccessor mca = (MinecraftClientAccessor) mc;
    private IMinecraftClient imc = (IMinecraftClient) mc;

    public AutoClicker() {
        super(Categories.Player, "auto-clicker", "Automatically clicks.");
    }

    @Override
    public void onActivate() {
        timer = 0;
        mc.options.keyAttack.setPressed(false);
        mc.options.keyUse.setPressed(false);
    }

    @Override
    public void onDeactivate() {
        mc.options.keyAttack.setPressed(false);
        mc.options.keyUse.setPressed(false);
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        switch (mode.get()) {
            case Hold:
                switch (button.get()) {
                    case Left:
                        mc.options.keyAttack.setPressed(true);
                        break;
                    case Right:
                        mc.options.keyUse.setPressed(true);
                        break;
                }
                break;
            case Press:
                timer++;
                if (delay.get() < timer) {
                    switch (button.get()) {
                        case Left:
                            Utils.leftClick();
                            break;
                        case Right:
                            Utils.rightClick();
                            break;
                    }
                    timer = 0;
                }
                break;
			case Manual:
				timer++;
				if (delay.get() < timer) {
					switch (button.get()) {
						case Left:
						if (mc.options.keyAttack.isPressed())
							mca.leftClick();
							break;
						case Right:
						if (mc.options.keyUse.isPressed())
							imc.rightClick();
							break;
					}
					timer = 0;
				}
        }
    }
}
