#!/bin/sh

docker run -it -u `id -u`:`id -g` -v `pwd`:/work knaou/mkdocs-uml mkdocs build
