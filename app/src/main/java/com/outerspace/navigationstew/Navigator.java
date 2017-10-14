package com.outerspace.navigationstew;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class Navigator {
    private final String FRAGMENT_PACKAGE = "com.outerspace.navigationstew.fragments.";

    private NavNode root;
    private NavNode currentNode;
    private HashMap<String, NavNode> nodes;
    private Stack<NavNode> navStack;
    private FragmentManager manager;
    private int containerViewId;

    public Navigator() {
        initGraph();
        navStack = new Stack<>();
    }

    private void initGraph() {
        nodes = new HashMap<>();

        String[][] arrInitNode = {
                // nodeId, Fragment class
                {"A", "FragmentA"},
                {"B", "FragmentB"},
                {"C", "FragmentC"},
                {"D", "FragmentD"},
                {"E", "FragmentE"},
                {"F", "FragmentF"},
                {"G", "FragmentG"},
                {"H", "FragmentH"}
        };

        String[][] arrPaths = {
                //nodeId, nodeId destination, can step back
                {"A", "B", "yes"},
                {"B", "C", "yes"},
                {"B", "E", "yes"},
                {"C", "D", "yes"},
                {"D", "G", "yes"},
                {"E", "F", "yes"},
                {"F", "G", "yes"},
                {"G", "H", "yes"},
                {"G", "B", "yes"},
                {"H", "A", "no"}
        };

        for(String[] arrIN: arrInitNode) {
            Class<?>  classreference = null;
            String nodeId = arrIN[0];
            String classname    = arrIN[1];
            try {
                classreference = Class.forName( FRAGMENT_PACKAGE + classname);
            }
            catch(ClassNotFoundException ex) {
                classreference = null;
            }
            NavNode node = new NavNode(nodeId, classreference);
            nodes.put(nodeId, node);
        }

        for(String[] arrPth: arrPaths) {
            NavNode nodeA = nodes.get(arrPth[0]);
            NavNode nodeB = nodes.get(arrPth[1]);
            boolean canstepback = arrPth[2].equalsIgnoreCase("yes");
            nodeA.paths.add(new NavPath(nodeB, canstepback));
        }
    }

    public void setRootFragment(String rootNameID, int containerViewId, FragmentManager manager) {
        this.manager = manager;
        this.containerViewId = containerViewId;
        root = nodes.get(rootNameID);
        currentNode = root;
        navStack.empty();
        activateCurrentNode();
    }

    private void activateCurrentNode() {

        try {
            Object obj = (Object) currentNode.fragmentClass.newInstance();
            Log.d("CLASS:", obj.getClass().getName());
            Fragment fragment;
            fragment = (Fragment) obj;
            FragmentTransaction transaction;
            transaction = manager.beginTransaction();
            transaction.add(containerViewId,  fragment);
            transaction.commit();
        }
        catch ( InstantiationException | IllegalAccessException ex) {
            return;
        }
    }

    public void walkTo(String walkToNodeId) throws ExceptionPathToNodeNotAvailable {
        for (NavPath path: currentNode.paths) {
            if(path.next.name.equalsIgnoreCase(walkToNodeId)) {
                if(path.reverse)
                    navStack.push(currentNode);
                else
                    navStack.removeAllElements();
                currentNode = path.next;
                activateCurrentNode();
                return;
            }
        }
        throw new ExceptionPathToNodeNotAvailable();
    }

    public void stepBack() {
        if(!navStack.isEmpty()) {
            currentNode = navStack.pop();
            activateCurrentNode();
        }
    }

    public boolean canStepBack() {
        return !navStack.isEmpty();
    }

    private class NavNode {
        public String name;
        public Class<?> fragmentClass;
        public ArrayList<NavPath> paths;

        public NavNode(String name, Class<?> fragmentClass) {
            this.name = name;
            this.fragmentClass = fragmentClass;
            this.paths = new ArrayList<>();
        };
    }

    private class NavPath {
        public NavNode next;
        public boolean reverse;

        public NavPath(NavNode next, boolean reverse) {
            this.next = next;
            this.reverse = reverse;
        }
    }
}
