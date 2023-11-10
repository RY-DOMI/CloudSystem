package net.labormc.cloudapi.sign;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.labormc.cloudapi.sign.enums.SignLayoutStates;

import java.util.List;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
@AllArgsConstructor
@Getter
@Setter
public class SignLayout {

    private SignLayoutStates state;
    private List<Layout> layouts;

    private byte blockColor;

    @AllArgsConstructor
    @Getter
    @Setter
    public class Layout {

        private String line1;
        private String line2;
        private String line3;
        private String line4;

        public SignLayout getLayout() {
            return SignLayout.this;
        }
    }

}
