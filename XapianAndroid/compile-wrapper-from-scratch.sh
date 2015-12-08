#!/bin/bash

ARCH=arm
PLATFORM=android-8
PREBUILD=$(pwd)/app/prebuild

TOOLCHAIN_DIR=$(mktemp -d)

mkdir -p "$PREBUILD"

# make toolchain
echo "Making toolchain..."

"$NDK_HOME/build/tools/make-standalone-toolchain.sh" --platform=$PLATFORM \
       --install-dir=$TOOLCHAIN_DIR --arch=$ARCH
export PATH="$TOOLCHAIN_DIR/bin:$PATH"
export PREFIX=$(ls "$TOOLCHAIN_DIR/bin"|grep 'gcc$'|sed 's/-gcc//g')
export CC="$PREFIX-gcc"
export CXX="$PREFIX-g++"

# fetch xapian, e2fsprogs, zlib
echo "Fetching dependencies..."
TEMP=$(mktemp -d)
cd $TEMP
wget -c http://oligarchy.co.uk/xapian/1.2.21/xapian-core-1.2.21.tar.xz
wget -c ftp://ftp.ntu.edu.tw/linux/kernel/people/tytso/e2fsprogs/v1.42.12/e2fsprogs-libs-1.42.12.tar.gz
wget -c http://zlib.net/zlib-1.2.8.tar.gz

tar xvf xapian-core-1.2.21.tar.xz
tar zxvf e2fsprogs-libs-1.42.12.tar.gz
tar zxvf zlib-1.2.8.tar.gz

# configure zlib and build
echo "building zlib..."
cd zlib-1.2.8
make clean
./configure
make
# copy zlib to prebuild
cp libz.a "$PREBUILD/libz.a"
cd ..

# configure uuid and build
echo "building libuuid..."
cd e2fsprogs-libs-1.42.12
./configure --host=$PREFIX
cd lib/uuid
make

# copy uuid to prebuild and to toolchain include and lib
mkdir "$TOOLCHAIN_DIR/include/c++/4.8/uuid"
cp uuid.h "$TOOLCHAIN_DIR/include/c++/4.8/uuid"
cp libuuid.a "$TOOLCHAIN_DIR/lib/gcc/$PREFIX/4.8/"
cp libuuid.a "$PREBUILD/libuuid.a"
cd ../../..

# configure xapian and build
echo "building xapian..."
cd xapian-core-1.2.21
./configure --host=$PREFIX --disable-shared
# fix config to get it to build
sed -ri 's/HAVE_DECL_SYS_NERR 1/HAVE_DECL_SYS_NERR 0/g' config.h
# now make it
make
# copy .libs/libxapian.a to prebuild
cp .libs/libxapian.a "$PREBUILD/libxapian.a"
cp -r include "$PREBUILD/include"
cd ..

# make ndk wrapper
echo "dependencies built. Now building ndk wrapper..."
cd "$PREBUILD/../jni"
ndk-build

# clean up
echo "cleanin up..."
rm -rf "$TOOLCHAIN_DIR"
rm -rf "$TEMP"
