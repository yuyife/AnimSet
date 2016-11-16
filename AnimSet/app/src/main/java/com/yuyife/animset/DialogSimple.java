package com.yuyife.animset;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.yuyife.animset.util.DialogEffects;
import com.yuyife.animset.wedgit.NiftyDialogBuilder;


public class DialogSimple extends Activity {

    private DialogEffects effect;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_simple);

    }

    public void dialogShow(View v) {
        NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(this);

        switch (v.getId()) {
            case R.id.fadein:
                effect = DialogEffects.Fadein;
                break;
            case R.id.slideright:
                effect = DialogEffects.Slideright;
                break;
            case R.id.slideleft:
                effect = DialogEffects.Slideleft;
                break;
            case R.id.slidetop:
                effect = DialogEffects.Slidetop;
                break;
            case R.id.slideBottom:
                effect = DialogEffects.SlideBottom;
                break;
            case R.id.newspager:
                effect = DialogEffects.Newspager;
                break;
            case R.id.fall:
                effect = DialogEffects.Fall;
                break;
            case R.id.sidefall:
                effect = DialogEffects.Sidefill;
                break;
            case R.id.fliph:
                effect = DialogEffects.Fliph;
                break;
            case R.id.flipv:
                effect = DialogEffects.Flipv;
                break;
            case R.id.rotatebottom:
                effect = DialogEffects.RotateBottom;
                break;
            case R.id.rotateleft:
                effect = DialogEffects.RotateLeft;
                break;
            case R.id.slit:
                effect = DialogEffects.Slit;
                break;
            case R.id.shake:
                effect = DialogEffects.Shake;
                break;
        }

        dialogBuilder
                .withTitle("Modal Dialog")                                  //.withTitle(null)  no title
                .withTitleColor("#FFFFFF")                                  //def
                .withDividerColor("#11000000")                              //def
                .withMessage("This is a modal Dialog.")                     //.withMessage(null)  no Msg
                .withMessageColor("#FFFFFFFF")                              //def  | withMessageColor(int resid)
                .withDialogColor("#FFE74C3C")                               //def  | withDialogColor(int resid)                               //def
                .withIcon(getResources().getDrawable(R.drawable.icon))
                .isCancelableOnTouchOutside(true)                           //def    | isCancelable(true)
                .withDuration(700)                                          //def
                .withEffect(effect)                                         //def BaseDialogEffects.Slidetop
                .withButton1Text("OK")                                      //def gone
                .withButton2Text("Cancel")                                  //def gone
                .setCustomView(R.layout.dialog_custom_view, v.getContext())         //.setCustomView(View or ResId,context)
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(v.getContext(), "i'm btn1", Toast.LENGTH_SHORT).show();
                    }
                })
                .setButton2Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(v.getContext(), "i'm btn2", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();

    }


}
