package org.mian.socketTool;

import org.mian.socketTool.util.CommandUtils;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner=new Scanner(System.in);
        CommandUtils.chooseServer(scanner);
    }



}