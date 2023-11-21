#include <iostream>
#include "Tree.h"

int main(int argc, char const *argv[])
{
    Tree* tree = new Tree();
    tree->addNode(1);
    tree->addNode(2);
    tree->addNode(3);
    tree->addEdge(1,2);
    tree->addEdge(2,3);
    tree->addEdge(1,3);
    tree->printTree();

    delete tree;
    return 0;
}
