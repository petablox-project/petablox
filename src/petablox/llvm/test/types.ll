%struct.ST = type { i32, double, %struct.RT }
%struct.RT = type { i8, [10 x [20 x i32]], i8 }

define void @bar(%struct.ST) {
    ret void
}

define double @foo(i32, double, float) {
    ret double 2.0;
}

define void @main() {
    %1 = alloca i1
    %2 = alloca i8
    %3 = alloca i32
    %4 = alloca i64
    %5 = alloca double
    %6 = alloca float
    %7 = alloca half
    %8 = alloca fp128
    %9 = alloca x86_fp80
    %10 = alloca ppc_fp128
    %11 = alloca x86_mmx
    %12 = alloca %struct.ST*, align 8
    %13 = alloca <4 x i32>
    %14 = call double @foo(i32 1, double 1.0, float 1.0)
    ret void
}