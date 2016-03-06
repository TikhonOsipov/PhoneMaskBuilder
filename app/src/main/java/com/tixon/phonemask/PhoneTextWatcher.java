package com.tixon.phonemask;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import java.util.ArrayList;

/**
 * @author Tikhon
 */

public class PhoneTextWatcher implements TextWatcher {

    private static final char STAR = '•';
    private static final char SPACE = ' ';
    private static final char RIGHT_BRACKET = ')';

    public static final int SYMBOL_STAR = 1;
    public static final int SYMBOL_SPACE = 2;

    public static final int DELIMITER_SPACE = 3;
    public static final int DELIMITER_DASH = 4;

    //main symbols
    private static final String S_STAR = "•";
    private static final String S_SPACE = " ";

    //delimiter symbols
    private static final String D_SPACE = " ";
    private static final String D_DASH = "-";

    //building values
    private String phonePrefix;
    private String phonePattern;
    private int indexLength;
    private int indexStartPosition;
    private int indexEndPosition;
    private ArrayList<Integer> patternPositions, indexes1, indexes2;
    private int phoneEndPosition;
    private String mainSymbol;
    private String delimiterSymbol;

    private int selectionDefault;

    private String hint;
    private int selection = -1;

    private int startPosition = 0;
    private boolean isDeleting = false;
    private String beforeString = null;

    private EditText editText;



    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        beforeString = String.valueOf(s);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //before 0, count 1 -- type
        //before 1, count 0 -- delete
        startPosition = start;

        //if typing
        if(before == 0 && count == 1) {
            isDeleting = false;

            editText.removeTextChangedListener(this);
            StringBuilder text = new StringBuilder(editText.getText().toString());

            //+7 (999) 123 45 67
            //   ^             ^
            if(startPosition <= indexStartPosition || startPosition > phoneEndPosition) {
                text.delete(startPosition, startPosition + 1);
            }

            //+7 (999) 123 45 67
            //       ^
            else if(startPosition == indexEndPosition) {
                String tempSymbol = text.substring(startPosition, startPosition + 1);
                text.replace(startPosition + 3, startPosition + 4, tempSymbol);
                text.delete(startPosition, startPosition + 1);
                //custom selection
                selection = startPosition + 3;
            }

            //+7 (999) 123 45 67
            //        ^   ^  ^

            /*
            startPosition == 8 ||
                    startPosition == 12 ||
                    startPosition == 15
             */
            else if(patternPositions.contains(startPosition)) {
                String tempSymbol = text.substring(startPosition, startPosition + 1);
                text.replace(startPosition + 2, startPosition + 3, tempSymbol);
                text.delete(startPosition, startPosition + 1);
                //custom selection
                selection = startPosition + 2;
            }
            else {
                text.replace(startPosition + 1, startPosition + 2, String.valueOf(text.charAt(startPosition)));
                text.delete(startPosition, startPosition + 1);
            }
            editText.setText(text.toString());
            editText.addTextChangedListener(this);

        }
        //if deleting
        else if(before == 1 && count == 0) {
            isDeleting = true;

            editText.removeTextChangedListener(this);
            StringBuilder text = new StringBuilder(editText.getText().toString());

            //+7 (999) 123 45 67
            //        ^   ^  ^
            /*
            startPosition == 8 ||
                    startPosition == 12 ||
                    startPosition == 15
             */
            if(patternPositions.contains(startPosition)) {
                text.insert(startPosition, delimiterSymbol);
            }

            //+7 (999) 123 45 67
            //       ^
            else if(startPosition == indexEndPosition) {
                text.insert(startPosition, RIGHT_BRACKET);
            }

            //+7 (999) 123 45 67
            //   ^
            else if(startPosition <= indexStartPosition) {
                text.insert(startPosition, beforeString.charAt(startPosition));
            }
            else {
                text.insert(startPosition, mainSymbol);
            }

            editText.setText(text.toString());
            editText.addTextChangedListener(this);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        //for typing in editText editText
        if(!isDeleting) {
            //6
            if(startPosition == indexEndPosition - 1) {
                editText.setSelection(startPosition + 3);
            }
            //startPosition == 11 || startPosition == 14
            else if(indexes1.contains(startPosition)) {
                editText.setSelection(startPosition + 2);//11 14
            }
            //3
            else if(startPosition <= indexStartPosition) {
                editText.setSelection(indexStartPosition + 1);
            }
            else if(startPosition > phoneEndPosition) {
                editText.setSelection(startPosition);
            }
            else {
                editText.setSelection(startPosition + 1);
            }
        }
        //for deleting in editText editText
        else {
            if(startPosition == patternPositions.get(0) + 1) {
                editText.setSelection(startPosition - 2);
            }

            /*
            startPosition == 8 ||
                    startPosition == 13 ||
                    startPosition == 16
             */
            else if(indexes2.contains(startPosition)) {
                editText.setSelection(startPosition - 1);
            }
            //3
            else if(startPosition <= indexStartPosition) {
                editText.setSelection(indexStartPosition + 1);
            }
            else {
                editText.setSelection(startPosition);
            }
        }
        //apply custom selection
        if(selection != -1) {
            editText.setSelection(selection);
            selection = -1; //set to default custom selection value
        }
    }

    public static class MaskBuilder {
        private EditText editText;

        private String hint = ""; //+7 (•••) ••• •• ••
        private String phonePrefix = "+7";
        private String phonePattern = "322";
        private int indexLength = 3;
        private int selectionDefault = 4;

        private int indexStartPosition = 3;
        private int indexEndPosition = 7;
        private ArrayList<Integer> patternPositions;
        private int phoneEndPosition = 17;

        private String mainSymbol = S_STAR;
        private String delimiterSymbol = D_SPACE;

        public MaskBuilder(EditText editText) {
            this.editText = editText;
            this.editText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {

                }
            });
        }

        private void initializeEditText() {
            editText.setText(hint);
            editText.setSelection(selectionDefault);
        }

        public MaskBuilder setPrefix(String prefix) {
            if(prefix != null) {
                if(prefix.isEmpty()) {
                    prefix = "+7";
                }
            } else {
                prefix = "+7";
            }
            phonePrefix = prefix;
            return this;
        }

        public MaskBuilder setIndexLength(int length) {
            if(length <= 0) {
                length = 3;
            }
            indexLength = length;
            //indexEndPosition = indexStartPosition + length + 1;
            return this;
        }

        public MaskBuilder setPattern(String pattern) {
            if(pattern != null) {
                if(pattern.isEmpty()) {
                    pattern = "322";
                }
            } else {
                pattern = "322";
            }
            phonePattern = pattern;
            return this;
        }

        public MaskBuilder setDelimiterSymbol(int symbol) {
            switch (symbol) {
                case DELIMITER_SPACE:
                    delimiterSymbol = D_SPACE;
                    break;
                case DELIMITER_DASH:
                    delimiterSymbol = D_DASH;
                    break;
                default:
                    delimiterSymbol = D_SPACE;
            }
            return this;
        }

        public MaskBuilder setMainSymbol(int symbol) {
            switch (symbol) {
                case SYMBOL_STAR:
                    mainSymbol = S_STAR;
                    break;
                case SYMBOL_SPACE:
                    mainSymbol = S_SPACE;
                    break;
                default:
                    mainSymbol = S_STAR;
            }
            return this;
        }

        public PhoneTextWatcher build() {
            return new PhoneTextWatcher(this);
        }
    }

    private PhoneTextWatcher(MaskBuilder builder) {
        phonePattern = builder.phonePattern; //322
        phonePrefix = builder.phonePrefix; //+7
        indexLength = builder.indexLength; //3

        indexStartPosition = phonePrefix.length() + 1;
        builder.selectionDefault = phonePrefix.length() + 2;
        selectionDefault = builder.selectionDefault;

        //indexStartPosition = builder.indexStartPosition; //3
        mainSymbol = builder.mainSymbol;
        delimiterSymbol = builder.delimiterSymbol;

        createPositions(builder);
        createPhoneEndPosition(builder);
        createHint(builder);
        createAssistantIndexes(builder);

        builder.initializeEditText();
        editText = builder.editText;
    }

    private void createPositions(MaskBuilder builder) {
        builder.indexEndPosition = indexStartPosition + builder.indexLength + 1;
        indexEndPosition = builder.indexEndPosition;

        builder.patternPositions = new ArrayList<>();
        builder.patternPositions.add(indexEndPosition + 1);
        for(int i = 0; i < builder.phonePattern.length() - 1; i++) {
            int position = builder.patternPositions.get(i) + Integer.parseInt(String.valueOf(builder.phonePattern.charAt(i)));
            builder.patternPositions.add(position + 1);
        }

        patternPositions = builder.patternPositions;
    }

    private void createHint(MaskBuilder builder) {
        builder.hint += builder.phonePrefix + " (";
        builder.hint += fillWithSymbols(builder.indexLength, builder.mainSymbol);
        builder.hint += ") ";

        builder.hint += fillWithSymbols(Integer.parseInt(String.valueOf(builder.phonePattern.charAt(0))), builder.mainSymbol);
        for(int i = 1; i < builder.phonePattern.length(); i++) {
            int length = Integer.parseInt(String.valueOf(builder.phonePattern.charAt(i)));
            builder.hint += builder.delimiterSymbol + fillWithSymbols(length, builder.mainSymbol);
        }
        hint = builder.hint;
    }

    private void createPhoneEndPosition(MaskBuilder builder) {
        builder.phoneEndPosition = indexStartPosition + builder.indexLength; //6

        for(int i = 0; i < builder.phonePattern.length(); i++) {
            int length = Integer.parseInt(String.valueOf(builder.phonePattern.charAt(i)));
            builder.phoneEndPosition += length;
        }
        builder.phoneEndPosition += patternPositions.size(); //spaces in pattern //+3 = 16
        builder.phoneEndPosition += 1; //brackets - 1 //+1 = 17
        phoneEndPosition = builder.phoneEndPosition;
    }

    private void createAssistantIndexes(MaskBuilder builder) {
        indexes1 = new ArrayList<>();
        indexes2 = new ArrayList<>();
        for(int i = 1; i < patternPositions.size(); i++) {
            indexes1.add(i-1, patternPositions.get(i) - 1);
        }

        for(int i = 0; i < patternPositions.size(); i++) {
            if(i > 0) {
                indexes2.add(patternPositions.get(i) + 1);
            } else {
                indexes2.add(patternPositions.get(i));
            }
        }
    }

    private String fillWithSymbols(int length, String symbol) {
        String result = "";
        for(int i = 0; i < length; i++) {
            result += symbol;
        }
        return result;
    }
}