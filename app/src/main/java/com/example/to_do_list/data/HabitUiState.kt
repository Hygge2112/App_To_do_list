package com.example.to_do_list.data

// Tệp mới: HabitUiState.kt

/**
 * Đại diện cho các trạng thái giao diện của màn hình hiển thị danh sách thói quen.
 */
sealed interface HabitUiState {
    /** Trạng thái đang tải dữ liệu. */
    object Loading : HabitUiState
    /** Trạng thái tải dữ liệu thành công. */
    data class Success(val habits: List<Habit>) : HabitUiState
    /** Trạng thái khi có lỗi xảy ra. */
    data class Error(val message: String) : HabitUiState
}