package com.mobilabsolutions.payment.android.newapi;

/*
 * Copyright 2013 Artur Mkrtchyan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.iban4j.CountryCode;
import org.iban4j.bban.BbanEntryType;
import org.iban4j.bban.BbanStructureEntry;
import org.junit.Test;

import java.util.*;

import io.reactivex.Observable;


/**
 * Class which represents bban structure, we used this to extract data for objective c code patterns
 * not really a test, but let's keep it as we might need to help out on objective c side as well
 *
 */
public class BbanStructureForObjCParser {

    private final BbanStructureEntry[] entries;

    private BbanStructureForObjCParser(final BbanStructureEntry... entries) {
        this.entries = entries;
    }


    private static final EnumMap<CountryCode, BbanStructureForObjCParser> structures;

    static {
        structures = new EnumMap<CountryCode, BbanStructureForObjCParser>(CountryCode.class);

        structures.put(CountryCode.AL,
                new BbanStructureForObjCParser(
                        BbanStructureEntry.bankCode(3, 'n'),
                        BbanStructureEntry.branchCode(4, 'n'),
                        BbanStructureEntry.nationalCheckDigit(1, 'n'),
                        BbanStructureEntry.accountNumber(16, 'c')));

        structures.put(CountryCode.AD,
                new BbanStructureForObjCParser(
                        BbanStructureEntry.bankCode(4, 'n'),
                        BbanStructureEntry.branchCode(4, 'n'),
                        BbanStructureEntry.accountNumber(12, 'c')));

        structures.put(CountryCode.AT,
                new BbanStructureForObjCParser(
                        BbanStructureEntry.bankCode(5, 'n'),
                        BbanStructureEntry.accountNumber(11, 'n')));


        structures.put(CountryCode.AZ,
                new BbanStructureForObjCParser(
                        BbanStructureEntry.bankCode(4, 'a'),
                        BbanStructureEntry.accountNumber(20, 'c')));

        structures.put(CountryCode.BH,
                new BbanStructureForObjCParser(
                        BbanStructureEntry.bankCode(4, 'a'),
                        BbanStructureEntry.accountNumber(14, 'c')));

        structures.put(CountryCode.BE,
                new BbanStructureForObjCParser(
                        BbanStructureEntry.bankCode(3, 'n'),
                        BbanStructureEntry.accountNumber(7, 'n'),
                        BbanStructureEntry.nationalCheckDigit(2, 'n')));

        structures.put(CountryCode.BA,
                new BbanStructureForObjCParser(
                        BbanStructureEntry.bankCode(3, 'n'),
                        BbanStructureEntry.branchCode(3, 'n'),
                        BbanStructureEntry.accountNumber(8, 'n'),
                        BbanStructureEntry.nationalCheckDigit(2, 'n')));

        structures.put(CountryCode.BR,
                new BbanStructureForObjCParser(
                        BbanStructureEntry.bankCode(8, 'n'),
                        BbanStructureEntry.branchCode(5, 'n'),
                        BbanStructureEntry.accountNumber(10, 'n'),
                        BbanStructureEntry.accountType(1, 'a'),
                        BbanStructureEntry.ownerAccountNumber(1, 'c')));

        structures.put(CountryCode.BG,
                new BbanStructureForObjCParser(
                        BbanStructureEntry.bankCode(4, 'a'),
                        BbanStructureEntry.branchCode(4, 'n'),
                        BbanStructureEntry.accountType(2, 'n'),
                        BbanStructureEntry.accountNumber(8, 'c')));

        structures.put(CountryCode.CR,
                new BbanStructureForObjCParser(
                        BbanStructureEntry.bankCode(3, 'n'),
                        BbanStructureEntry.accountNumber(14, 'n')));

        structures.put(CountryCode.DE,
                new BbanStructureForObjCParser(
                        BbanStructureEntry.bankCode(8, 'n'),
                        BbanStructureEntry.accountNumber(10, 'n')));

        structures.put(CountryCode.HR,
                new BbanStructureForObjCParser(
                        BbanStructureEntry.bankCode(7, 'n'),
                        BbanStructureEntry.accountNumber(10, 'n')));

        structures.put(CountryCode.CY,
                new BbanStructureForObjCParser(
                        BbanStructureEntry.bankCode(3, 'n'),
                        BbanStructureEntry.branchCode(5, 'n'),
                        BbanStructureEntry.accountNumber(16, 'c')));

        structures.put(CountryCode.CZ,
                new BbanStructureForObjCParser(
                        BbanStructureEntry.bankCode(4, 'n'),
                        BbanStructureEntry.accountNumber(16, 'n')));

        structures.put(CountryCode.DK,
                new BbanStructureForObjCParser(
                        BbanStructureEntry.bankCode(4, 'n'),
                        BbanStructureEntry.accountNumber(10, 'n')));

        structures.put(CountryCode.DO,
                new BbanStructureForObjCParser(
                        BbanStructureEntry.bankCode(4, 'c'),
                        BbanStructureEntry.accountNumber(20, 'n')));

        structures.put(CountryCode.EE,
                new BbanStructureForObjCParser(
                        BbanStructureEntry.bankCode(2, 'n'),
                        BbanStructureEntry.branchCode(2, 'n'),
                        BbanStructureEntry.accountNumber(11, 'n'),
                        BbanStructureEntry.nationalCheckDigit(1, 'n')));

        structures.put(CountryCode.FO,
                new BbanStructureForObjCParser(
                        BbanStructureEntry.bankCode(4, 'n'),
                        BbanStructureEntry.accountNumber(9, 'n'),
                        BbanStructureEntry.nationalCheckDigit(1, 'n')));

        structures.put(CountryCode.FI,
                new BbanStructureForObjCParser(
                        BbanStructureEntry.bankCode(6, 'n'),
                        BbanStructureEntry.accountNumber(7, 'n'),
                        BbanStructureEntry.nationalCheckDigit(1, 'n')));

        structures.put(CountryCode.FR,
                new BbanStructureForObjCParser(
                        BbanStructureEntry.bankCode(5, 'n'),
                        BbanStructureEntry.branchCode(5, 'n'),
                        BbanStructureEntry.accountNumber(11, 'c'),
                        BbanStructureEntry.nationalCheckDigit(2, 'n')));

        structures.put(CountryCode.GE,
                new BbanStructureForObjCParser(
                        BbanStructureEntry.bankCode(2, 'a'),
                        BbanStructureEntry.accountNumber(16, 'n')));

        structures.put(CountryCode.GI,
                new BbanStructureForObjCParser(
                        BbanStructureEntry.bankCode(4, 'a'),
                        BbanStructureEntry.accountNumber(15, 'c')));

        structures.put(CountryCode.GL,
                new BbanStructureForObjCParser(
                        BbanStructureEntry.bankCode(4, 'n'),
                        BbanStructureEntry.accountNumber(10, 'n')));

        structures.put(CountryCode.GR,
                new BbanStructureForObjCParser(
                        BbanStructureEntry.bankCode(3, 'n'),
                        BbanStructureEntry.branchCode(4, 'n'),
                        BbanStructureEntry.accountNumber(16, 'c')));

        structures.put(CountryCode.GT,
                new BbanStructureForObjCParser(
                        BbanStructureEntry.bankCode(4, 'c'),
                        BbanStructureEntry.accountNumber(20, 'c')));

        structures.put(CountryCode.HU,
                new BbanStructureForObjCParser(
                        BbanStructureEntry.bankCode(3, 'n'),
                        BbanStructureEntry.branchCode(4, 'n'),
                        BbanStructureEntry.accountNumber(16, 'n'),
                        BbanStructureEntry.nationalCheckDigit(1, 'n')));

        structures.put(CountryCode.IS,
                new BbanStructureForObjCParser(
                        BbanStructureEntry.bankCode(4, 'n'),
                        BbanStructureEntry.branchCode(2, 'n'),
                        BbanStructureEntry.accountNumber(6, 'n'),
                        BbanStructureEntry.identificationNumber(10, 'n')));

        structures.put(CountryCode.IE,
                new BbanStructureForObjCParser(
                        BbanStructureEntry.bankCode(4, 'a'),
                        BbanStructureEntry.branchCode(6, 'n'),
                        BbanStructureEntry.accountNumber(8, 'n')));

        structures.put(CountryCode.IL,
                new BbanStructureForObjCParser(
                        BbanStructureEntry.bankCode(3, 'n'),
                        BbanStructureEntry.branchCode(3, 'n'),
                        BbanStructureEntry.accountNumber(13, 'n')));

        structures.put(CountryCode.IR,
                new BbanStructureForObjCParser(
                        BbanStructureEntry.bankCode(3, 'n'),
                        BbanStructureEntry.accountNumber(19, 'n')));

        structures.put(CountryCode.IT,
                new BbanStructureForObjCParser(
                        BbanStructureEntry.nationalCheckDigit(1, 'a'),
                        BbanStructureEntry.bankCode(5, 'n'),
                        BbanStructureEntry.branchCode(5, 'n'),
                        BbanStructureEntry.accountNumber(12, 'c')));

        structures.put(CountryCode.JO,
                new BbanStructureForObjCParser(
                        BbanStructureEntry.bankCode(4, 'a'),
                        BbanStructureEntry.branchCode(4, 'n'),
                        BbanStructureEntry.accountNumber(18, 'c')));

        structures.put(CountryCode.KZ,
                new BbanStructureForObjCParser(
                        BbanStructureEntry.bankCode(3, 'n'),
                        BbanStructureEntry.accountNumber(13, 'c')));

        structures.put(CountryCode.KW,
                new BbanStructureForObjCParser(
                        BbanStructureEntry.bankCode(4, 'a'),
                        BbanStructureEntry.accountNumber(22, 'c')));

        structures.put(CountryCode.LV,
                new BbanStructureForObjCParser(
                        BbanStructureEntry.bankCode(4, 'a'),
                        BbanStructureEntry.accountNumber(13, 'c')));

        structures.put(CountryCode.LB,
                new BbanStructureForObjCParser(
                        BbanStructureEntry.bankCode(4, 'n'),
                        BbanStructureEntry.accountNumber(20, 'c')));

        structures.put(CountryCode.LI,
                new BbanStructureForObjCParser(
                        BbanStructureEntry.bankCode(5, 'n'),
                        BbanStructureEntry.accountNumber(12, 'c')));

        structures.put(CountryCode.LT,
                new BbanStructureForObjCParser(
                        BbanStructureEntry.bankCode(5, 'n'),
                        BbanStructureEntry.accountNumber(11, 'n')));

        structures.put(CountryCode.LU,
                new BbanStructureForObjCParser(
                        BbanStructureEntry.bankCode(3, 'n'),
                        BbanStructureEntry.accountNumber(13, 'c')));

        structures.put(CountryCode.MK,
                new BbanStructureForObjCParser(
                        BbanStructureEntry.bankCode(3, 'n'),
                        BbanStructureEntry.accountNumber(10, 'c'),
                        BbanStructureEntry.nationalCheckDigit(2, 'n')));

        structures.put(CountryCode.MT,
                new BbanStructureForObjCParser(
                        BbanStructureEntry.bankCode(4, 'a'),
                        BbanStructureEntry.branchCode(5, 'n'),
                        BbanStructureEntry.accountNumber(18, 'c')));

        structures.put(CountryCode.MR,
                new BbanStructureForObjCParser(
                        BbanStructureEntry.bankCode(5, 'n'),
                        BbanStructureEntry.branchCode(5, 'n'),
                        BbanStructureEntry.accountNumber(11, 'n'),
                        BbanStructureEntry.nationalCheckDigit(2, 'n')));

        structures.put(CountryCode.MU,
                new BbanStructureForObjCParser(
                        BbanStructureEntry.bankCode(6, 'c'),
                        BbanStructureEntry.branchCode(2, 'n'),
                        BbanStructureEntry.accountNumber(18, 'c')));

        structures.put(CountryCode.MD,
                new BbanStructureForObjCParser(
                        BbanStructureEntry.bankCode(2, 'c'),
                        BbanStructureEntry.accountNumber(18, 'c')));

        structures.put(CountryCode.MC,
                new BbanStructureForObjCParser(
                        BbanStructureEntry.bankCode(5, 'n'),
                        BbanStructureEntry.branchCode(5, 'n'),
                        BbanStructureEntry.accountNumber(11, 'c'),
                        BbanStructureEntry.nationalCheckDigit(2, 'n')));

        structures.put(CountryCode.ME,
                new BbanStructureForObjCParser(
                        BbanStructureEntry.bankCode(3, 'n'),
                        BbanStructureEntry.accountNumber(13, 'n'),
                        BbanStructureEntry.nationalCheckDigit(2, 'n')));

        structures.put(CountryCode.NL,
                new BbanStructureForObjCParser(
                        BbanStructureEntry.bankCode(4, 'a'),
                        BbanStructureEntry.accountNumber(10, 'n')));

        structures.put(CountryCode.NO,
                new BbanStructureForObjCParser(
                        BbanStructureEntry.bankCode(4, 'n'),
                        BbanStructureEntry.accountNumber(6, 'n'),
                        BbanStructureEntry.nationalCheckDigit(1, 'n')));

        structures.put(CountryCode.PK,
                new BbanStructureForObjCParser(
                        BbanStructureEntry.bankCode(4, 'c'),
                        BbanStructureEntry.accountNumber(16, 'n')));

        structures.put(CountryCode.PS,
                new BbanStructureForObjCParser(
                        BbanStructureEntry.bankCode(4, 'a'),
                        BbanStructureEntry.accountNumber(21, 'c')));

        structures.put(CountryCode.PL,
                new BbanStructureForObjCParser(
                        BbanStructureEntry.bankCode(3, 'n'),
                        BbanStructureEntry.branchCode(4, 'n'),
                        BbanStructureEntry.nationalCheckDigit(1, 'n'),
                        BbanStructureEntry.accountNumber(16, 'n')));

        structures.put(CountryCode.PT,
                new BbanStructureForObjCParser(
                        BbanStructureEntry.bankCode(4, 'n'),
                        BbanStructureEntry.branchCode(4, 'n'),
                        BbanStructureEntry.accountNumber(11, 'n'),
                        BbanStructureEntry.nationalCheckDigit(2, 'n')));

        structures.put(CountryCode.RO,
                new BbanStructureForObjCParser(
                        BbanStructureEntry.bankCode(4, 'a'),
                        BbanStructureEntry.accountNumber(16, 'c')));

        structures.put(CountryCode.QA,
                new BbanStructureForObjCParser(
                        BbanStructureEntry.bankCode(4, 'a'),
                        BbanStructureEntry.accountNumber(21, 'c')));

        structures.put(CountryCode.SM,
                new BbanStructureForObjCParser(
                        BbanStructureEntry.nationalCheckDigit(1, 'a'),
                        BbanStructureEntry.bankCode(5, 'n'),
                        BbanStructureEntry.branchCode(5, 'n'),
                        BbanStructureEntry.accountNumber(12, 'c')));

        structures.put(CountryCode.SA,
                new BbanStructureForObjCParser(
                        BbanStructureEntry.bankCode(2, 'n'),
                        BbanStructureEntry.accountNumber(18, 'c')));

        structures.put(CountryCode.RS,
                new BbanStructureForObjCParser(
                        BbanStructureEntry.bankCode(3, 'n'),
                        BbanStructureEntry.accountNumber(13, 'n'),
                        BbanStructureEntry.nationalCheckDigit(2, 'n')));

        structures.put(CountryCode.SK,
                new BbanStructureForObjCParser(
                        BbanStructureEntry.bankCode(4, 'n'),
                        BbanStructureEntry.accountNumber(16, 'n')));

        structures.put(CountryCode.SI,
                new BbanStructureForObjCParser(
                        BbanStructureEntry.bankCode(2, 'n'),
                        BbanStructureEntry.branchCode(3, 'n'),
                        BbanStructureEntry.accountNumber(8, 'n'),
                        BbanStructureEntry.nationalCheckDigit(2, 'n')));

        structures.put(CountryCode.ES,
                new BbanStructureForObjCParser(
                        BbanStructureEntry.bankCode(4, 'n'),
                        BbanStructureEntry.branchCode(4, 'n'),
                        BbanStructureEntry.nationalCheckDigit(2, 'n'),
                        BbanStructureEntry.accountNumber(10, 'n')));

        structures.put(CountryCode.SE,
                new BbanStructureForObjCParser(
                        BbanStructureEntry.bankCode(3, 'n'),
                        BbanStructureEntry.accountNumber(17, 'n')));

        structures.put(CountryCode.CH,
                new BbanStructureForObjCParser(
                        BbanStructureEntry.bankCode(5, 'n'),
                        BbanStructureEntry.accountNumber(12, 'c')));

        structures.put(CountryCode.TN,
                new BbanStructureForObjCParser(
                        BbanStructureEntry.bankCode(2, 'n'),
                        BbanStructureEntry.branchCode(3, 'n'),
                        BbanStructureEntry.accountNumber(15, 'c')));

        structures.put(CountryCode.TR,
                new BbanStructureForObjCParser(
                        BbanStructureEntry.bankCode(5, 'n'),
                        BbanStructureEntry.nationalCheckDigit(1, 'c'),
                        BbanStructureEntry.accountNumber(16, 'c')));

        structures.put(CountryCode.UA,
                new BbanStructureForObjCParser(
                        BbanStructureEntry.bankCode(6, 'n'),
                        BbanStructureEntry.accountNumber(19, 'n')));

        structures.put(CountryCode.GB,
                new BbanStructureForObjCParser(
                        BbanStructureEntry.bankCode(4, 'a'),
                        BbanStructureEntry.branchCode(6, 'n'),
                        BbanStructureEntry.accountNumber(8, 'n')));

        structures.put(CountryCode.AE,
                new BbanStructureForObjCParser(
                        BbanStructureEntry.bankCode(3, 'n'),
                        BbanStructureEntry.accountNumber(16, 'c')));

        structures.put(CountryCode.VG,
                new BbanStructureForObjCParser(
                        BbanStructureEntry.bankCode(4, 'c'),
                        BbanStructureEntry.accountNumber(16, 'n')));

        structures.put(CountryCode.TL,
                new BbanStructureForObjCParser(
                        BbanStructureEntry.bankCode(3, 'n'),
                        BbanStructureEntry.accountNumber(14, 'n'),
                        BbanStructureEntry.nationalCheckDigit(2, 'n')));

        structures.put(CountryCode.XK,
                new BbanStructureForObjCParser(
                        BbanStructureEntry.bankCode(2, 'n'),
                        BbanStructureEntry.branchCode(2, 'n'),
                        BbanStructureEntry.accountNumber(10, 'n'),
                        BbanStructureEntry.nationalCheckDigit(2, 'n')));

    }

    /**
     * @param countryCode the country code.
     * @return BbanStructureForObjCParser for specified country or null if country is not supported.
     */
    public static BbanStructureForObjCParser forCountry(final CountryCode countryCode) {
        return structures.get(countryCode);
    }

    public List<BbanStructureEntry> getEntries() {
        return Collections.unmodifiableList(Arrays.asList(entries));
    }

    public static List<CountryCode> supportedCountries() {
        final List<CountryCode> countryCodes = new ArrayList<CountryCode>(structures.size());
        countryCodes.addAll(structures.keySet());
        return Collections.unmodifiableList(countryCodes);
    }

    /**
     * Returns the length of bban.
     *
     * @return int length
     */
    public int getBbanLength() {
        int length = 0;

        for (BbanStructureEntry entry : entries) {
            length += entry.getLength();
        }

        return length;
    }

    public static void print() {
        System.out.println("@{");
        Observable.fromIterable(structures.entrySet())
                .map(
                        entry -> {
                            String row = "";
                            row += entry.getKey().getAlpha2();
                            row += ",";
                            int bankCodeStart = extractBorderStart(forCountry(entry.getKey()), BbanEntryType.bank_code);
                            int bankCodeEnd = extractBorderEnd(forCountry(entry.getKey()), BbanEntryType.bank_code);
                            int branchCodeStart = extractBorderStart(forCountry(entry.getKey()), BbanEntryType.branch_code);
                            int branchCodeEnd = extractBorderEnd(forCountry(entry.getKey()), BbanEntryType.branch_code);

                            int identificationNumberStart = extractBorderStart(forCountry(entry.getKey()), BbanEntryType.identification_number);
                            int identificationNumberEnd = extractBorderEnd(forCountry(entry.getKey()), BbanEntryType.identification_number);


                            int bankNumberStart = bankCodeStart;
                            int bankNumberEnd = bankCodeEnd;
                            if (branchCodeStart != -1) {
                                bankNumberEnd = branchCodeEnd;
                            }
                            int accountBorderStart = extractBorderStart(forCountry(entry.getKey()), BbanEntryType.account_number);
                            int accountBorderEnd = extractBorderEnd(forCountry(entry.getKey()), BbanEntryType.account_number);
                            if (identificationNumberStart != -1) {
                                accountBorderEnd = identificationNumberEnd;
                            }
//                            System.out.println(
//                                    entry.getKey().getName() + " " + entry.getKey().getAlpha2() + " Bank number start: " + bankNumberStart + " Bank number end: " + bankNumberEnd
//                                            + " Account start: " + accountBorderStart + " Account end: " + accountBorderEnd
//
//
//
//                            );

                            System.out.println(
                                    "\t@\"" + entry.getKey().getAlpha2() + "\" : " + "@{"
                                            +"\n\t\t @\"bankNumberStart\" : @" + bankNumberStart + "," +
                                            "\n\t\t @\"bankNumberEnd:\" : @" + bankNumberEnd + "," +
                                            "\n\t\t @\"accountStart:\" : @" + accountBorderStart + ", " +
                                            "\n\t\t @\"accountEnd:\" : @" + accountBorderEnd +
                                            "\n\t},"
                            );

                            return "Bla";
                        }
                ).blockingLast();
        System.out.println("};");

    }





    private static int extractBorderStart(BbanStructureForObjCParser bbanStructureForObjCParser, BbanEntryType entryType){

        int bbanEntryOffset = 4;
        for(final BbanStructureEntry entry : bbanStructureForObjCParser.getEntries()) {
            final int entryLength = entry.getLength();



            if(entry.getEntryType() == entryType) {
                return bbanEntryOffset;
            }
            bbanEntryOffset = bbanEntryOffset + entryLength;
        }
        return -1;
    }

    private static int extractBorderEnd(BbanStructureForObjCParser bbanStructureForObjCParser, BbanEntryType entryType){

        int bbanEntryOffset = 4;
        for(final BbanStructureEntry entry : bbanStructureForObjCParser.getEntries()) {
            final int entryLength = entry.getLength();


            bbanEntryOffset = bbanEntryOffset + entryLength;
            if(entry.getEntryType() == entryType) {
                return bbanEntryOffset;
            }
        }
        return -1;
    }


}

