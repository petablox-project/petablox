define void @bar() {
    ret void
}

define hidden dllimport fastcc noalias i32 @foo(i32 returned %x) noinline section "test" gc "shadow-stack" personality void()* @bar {
    ret i32 %x
}

define void @main() {
    ret void
}