import tkinter as tk
from tkinter import filedialog, messagebox, ttk
from PIL import Image, ImageTk
import numpy as np


class ImageProcessingApp:
    def __init__(self, root):
        self.root = root
        self.root.title("Лабораторная работа № 4 — Алгоритмы обработки изображений")
        self.root.geometry("1350x880")

        self.orig_img_pil = None
        self.gray_arr = None
        self.bin_arr = None

        self.create_widgets()
        self.generate_default_image()

    def create_widgets(self):
        # Верхняя панель управления
        control_frame = tk.LabelFrame(self.root, text="Параметры управления", padx=10, pady=8)
        control_frame.pack(side=tk.TOP, fill=tk.X, padx=10, pady=5)

        # Кнопка загрузки изображения
        btn_load = tk.Button(
            control_frame, text="Загрузить изображение", command=self.load_image,
            bg="#dceeff", font=("Arial", 9, "bold"), padx=8, pady=4
        )
        btn_load.grid(row=0, column=0, rowspan=2, padx=10, pady=5)

        # Задание 1: Порог бинаризации
        tk.Label(control_frame, text="Задание 1 (Порог бинаризации 0-255):").grid(row=0, column=1, sticky="e", padx=5)
        self.thresh_bin_var = tk.IntVar(value=128)
        tk.Spinbox(control_frame, from_=0, to=255, textvariable=self.thresh_bin_var, width=6).grid(row=0, column=2, sticky="w", padx=5)

        # Задание 2 (Вариант 5): Порог неполной пороговой обработки
        tk.Label(control_frame, text="Задание 2, Вар. 5 (Порог неполной обр. 0-255):").grid(row=1, column=1, sticky="e", padx=5)
        self.thresh_z2_var = tk.IntVar(value=120)
        tk.Spinbox(control_frame, from_=0, to=255, textvariable=self.thresh_z2_var, width=6).grid(row=1, column=2, sticky="w", padx=5)

        # Задание 4: Параметры морфологии (Размер, форма ядра, якорь)
        tk.Label(control_frame, text="Задание 4 (Операция):").grid(row=0, column=3, sticky="e", padx=5)
        self.morph_op_var = tk.StringVar(value="Вариант 1: Расширение (дилатация)")
        morph_op_combo = ttk.Combobox(
            control_frame, textvariable=self.morph_op_var, state="readonly", width=32,
            values=[
                "Вариант 1: Расширение (дилатация)",
                "Вариант 5: Внешнее оконтуривание"
            ]
        )
        morph_op_combo.grid(row=0, column=4, columnspan=3, sticky="w", padx=5)

        tk.Label(control_frame, text="Форма ядра:").grid(row=1, column=3, sticky="e", padx=5)
        self.kernel_shape_var = tk.StringVar(value="Прямоугольник")
        shape_combo = ttk.Combobox(
            control_frame, textvariable=self.kernel_shape_var, state="readonly", width=14,
            values=["Прямоугольник", "Крест", "Ромб"]
        )
        shape_combo.grid(row=1, column=4, sticky="w", padx=5)

        tk.Label(control_frame, text="Размер ядра (W x H):").grid(row=1, column=5, sticky="e", padx=5)
        size_frame = tk.Frame(control_frame)
        size_frame.grid(row=1, column=6, sticky="w", padx=5)
        self.kw_var = tk.IntVar(value=3)
        self.kh_var = tk.IntVar(value=3)
        tk.Spinbox(size_frame, from_=1, to=15, textvariable=self.kw_var, width=4).pack(side=tk.LEFT)
        tk.Label(size_frame, text="x").pack(side=tk.LEFT)
        tk.Spinbox(size_frame, from_=1, to=15, textvariable=self.kh_var, width=4).pack(side=tk.LEFT)

        tk.Label(control_frame, text="Якорь (X, Y):").grid(row=0, column=7, sticky="e", padx=5)
        anchor_frame = tk.Frame(control_frame)
        anchor_frame.grid(row=0, column=8, sticky="w", padx=5)
        self.ax_var = tk.IntVar(value=1)
        self.ay_var = tk.IntVar(value=1)
        tk.Spinbox(anchor_frame, from_=0, to=14, textvariable=self.ax_var, width=4).pack(side=tk.LEFT)
        tk.Label(anchor_frame, text=",").pack(side=tk.LEFT)
        tk.Spinbox(anchor_frame, from_=0, to=14, textvariable=self.ay_var, width=4).pack(side=tk.LEFT)

        tk.Label(control_frame, text="Цвет объекта (бинарн.):").grid(row=1, column=7, sticky="e", padx=5)
        self.fg_color_var = tk.StringVar(value="Белый (255)")
        fg_combo = ttk.Combobox(
            control_frame, textvariable=self.fg_color_var, state="readonly", width=13,
            values=["Белый (255)", "Черный (0)"]
        )
        fg_combo.grid(row=1, column=8, sticky="w", padx=5)

        # Кнопка применения параметров
        btn_apply = tk.Button(
            control_frame, text="Применить / Обновить", command=self.process_all,
            bg="#d4f7d4", font=("Arial", 9, "bold"), padx=10, pady=4
        )
        btn_apply.grid(row=0, column=9, rowspan=2, padx=15, pady=5)

        # Область отображения 6 изображений (сетка 2x3)
        self.grid_frame = tk.Frame(self.root)
        self.grid_frame.pack(side=tk.TOP, fill=tk.BOTH, expand=True, padx=10, pady=5)

        self.img_labels = []
        self.title_labels = []
        titles = [
            "1. Исходное изображение",
            "2. Оттенки серого (Задание 1)",
            "3. Пороговая обработка (Задание 1)",
            "4. Неполная пороговая обработка (Задание 2, Вар. 5)",
            "5. Повышение контрастности (Задание 3, Вар. 5)",
            "6. Морфологическая обработка (Задание 4)"
        ]

        for i in range(6):
            r = i // 3
            c = i % 3
            cell = tk.Frame(self.grid_frame, bd=2, relief=tk.GROOVE, padx=5, pady=5)
            cell.grid(row=r, column=c, sticky="nsew", padx=5, pady=5)
            self.grid_frame.grid_columnconfigure(c, weight=1)
            self.grid_frame.grid_rowconfigure(r, weight=1)

            lbl_title = tk.Label(cell, text=titles[i], font=("Arial", 10, "bold"))
            lbl_title.pack(side=tk.TOP, pady=3)
            self.title_labels.append(lbl_title)

            lbl_img = tk.Label(cell, bg="#ebebeb")
            lbl_img.pack(side=tk.TOP, fill=tk.BOTH, expand=True)
            self.img_labels.append(lbl_img)

    def generate_default_image(self):
        """Генерирует тестовое изображение при запуске, чтобы программа сразу работала."""
        w, h = 320, 240
        arr = np.zeros((h, w, 3), dtype=np.uint8)
        # Градиентный фон
        for y in range(h):
            for x in range(w):
                arr[y, x] = [(x * 255) // w, (y * 255) // h, 140]
        arr[40:110, 40:120] = [240, 240, 240]
        arr[65:85, 65:95] = [30, 30, 30]
        yy, xx = np.ogrid[:h, :w]
        circle = (xx - 220) ** 2 + (yy - 140) ** 2 <= 55 ** 2
        arr[circle] = [255, 220, 60]
        self.orig_img_pil = Image.fromarray(arr)
        self.process_all()

    def load_image(self):
        path = filedialog.askopenfilename(
            title="Выберите изображение",
            filetypes=[("Images", "*.png;*.jpg;*.jpeg;*.bmp;*.tif"), ("All files", "*.*")]
        )
        if path:
            img = Image.open(path).convert("RGB")
            # Ограничим размер для быстрой матричной обработки в учебном примере
            img.thumbnail((450, 350), Image.Resampling.LANCZOS)
            self.orig_img_pil = img
            self.process_all()

    # АЛГОРИТМЫ ОБРАБОТКИ

    def to_grayscale(self, rgb_arr):
        """Задание 1: Перевод цветного изображения в оттенки серого."""
        gray = (
            0.299 * rgb_arr[:, :, 0] +
            0.587 * rgb_arr[:, :, 1] +
            0.114 * rgb_arr[:, :, 2]
        )
        return np.clip(gray, 0, 255).astype(np.uint8)

    def threshold_binarize(self, gray_arr, threshold):
        """Задание 1: Пороговая обработка (бинаризация)."""
        return np.where(gray_arr >= threshold, 255, 0).astype(np.uint8)

    def incomplete_threshold(self, gray_arr, threshold):
        """
        Задание 2, Вариант 5: Неполная пороговая обработка
        """
        return np.where(gray_arr >= threshold, 255, gray_arr).astype(np.uint8)

    def mask_filter_task3_var5(self, gray_arr):
        """
        Задание 3, Вариант 5: Линейная масочная фильтрация
        """
        A = 0.0
        B = 1.0
        M = np.array([
            [0, -1,  0],
            [-1, 5, -1],
            [0, -1,  0]
        ], dtype=np.float32)

        # Копирование крайних рядов на внешний слой
        padded = np.pad(gray_arr.astype(np.float32), pad_width=1, mode='edge')
        h, w = gray_arr.shape

        # Векторизованная свертка с матрицей 3x3
        acc = np.zeros((h, w), dtype=np.float32)
        for i in range(3):
            for j in range(3):
                if M[i, j] != 0:
                    acc += padded[i:i + h, j:j + w] * M[i, j]

        res = A + B * acc
        return np.clip(res, 0, 255).astype(np.uint8)

    def build_structuring_element(self, kw, kh, shape_name):
        """Построение структурного элемента для Задания 4."""
        kernel = np.ones((kh, kw), dtype=bool)
        cy, cx = kh // 2, kw // 2
        if shape_name == "Крест":
            kernel[:, :] = False
            kernel[cy, :] = True
            kernel[:, cx] = True
        elif shape_name == "Ромб":
            for r in range(kh):
                for c in range(kw):
                    dist = (abs(r - cy) / max(cy, 1)) + (abs(c - cx) / max(cx, 1))
                    kernel[r, c] = (dist <= 1.01)
        return kernel

    def dilate_binary(self, bin_arr, kernel, ax, ay, fg_is_white=True):
        """
        Базовая операция расширения с учетом размера, формы ядра и якоря (ax, ay).
        """
        kh, kw = kernel.shape
        h, w = bin_arr.shape

        # Переводим бинарное изображение в булеву маску объекта (True = объект)
        obj_mask = (bin_arr == 255) if fg_is_white else (bin_arr == 0)

        # Отступы вокруг изображения с учетом позиции якоря (ay, ax)
        pad_top = ay
        pad_bottom = kh - 1 - ay
        pad_left = ax
        pad_right = kw - 1 - ax

        padded = np.pad(obj_mask, ((pad_top, pad_bottom), (pad_left, pad_right)), mode='edge')
        dilated = np.zeros((h, w), dtype=bool)

        for r in range(kh):
            for c in range(kw):
                if kernel[r, c]:
                    dilated |= padded[r:r + h, c:c + w]

        if fg_is_white:
            return np.where(dilated, 255, 0).astype(np.uint8)
        else:
            return np.where(dilated, 0, 255).astype(np.uint8)

    def process_all(self):
        if self.orig_img_pil is None:
            return

        kw = max(1, self.kw_var.get())
        kh = max(1, self.kh_var.get())
        ax = self.ax_var.get()
        ay = self.ay_var.get()

        if not (0 <= ax < kw and 0 <= ay < kh):
            messagebox.showwarning(
                "Ошибка параметров ядра",
                f"Координаты якоря (X={ax}, Y={ay}) должны находиться внутри ядра "
                f"размером {kw}x{kh}:\n0 <= X < {kw}, 0 <= Y < {kh}.\n"
                f"Якорь будет автоматически установлен в центр."
            )
            ax = kw // 2
            ay = kh // 2
            self.ax_var.set(ax)
            self.ay_var.set(ay)

        thresh_bin = np.clip(self.thresh_bin_var.get(), 0, 255)
        thresh_z2 = np.clip(self.thresh_z2_var.get(), 0, 255)

        rgb_arr = np.array(self.orig_img_pil)

        # 1. Оттенки серого
        self.gray_arr = self.to_grayscale(rgb_arr)

        # 2. Пороговая обработка
        self.bin_arr = self.threshold_binarize(self.gray_arr, thresh_bin)

        # 3. Задание 2 (Вариант 5): Неполная пороговая обработка
        z2_arr = self.incomplete_threshold(self.gray_arr, thresh_z2)

        # 4. Задание 3 (Вариант 5): Масочная фильтрация (повышение контрастности)
        z3_arr = self.mask_filter_task3_var5(self.gray_arr)

        # 5. Задание 4: Морфологическая операция
        kernel = self.build_structuring_element(kw, kh, self.kernel_shape_var.get())
        fg_is_white = (self.fg_color_var.get() == "Белый (255)")
        dilated_arr = self.dilate_binary(self.bin_arr, kernel, ax, ay, fg_is_white)

        op_name = self.morph_op_var.get()
        if "Вариант 1" in op_name:
            # Вариант 1: Расширение (дилатация)
            z4_arr = dilated_arr
            self.title_labels[5].config(text="6. Расширение / Дилатация (Задание 4, Вар. 1)")
        else:
            # Вариант 5: Внешнее оконтуривание = Дилатация(A) \ A
            if fg_is_white:
                edge_mask = (dilated_arr == 255) & (self.bin_arr == 0)
                z4_arr = np.where(edge_mask, 255, 0).astype(np.uint8)
            else:
                edge_mask = (dilated_arr == 0) & (self.bin_arr == 255)
                z4_arr = np.where(edge_mask, 0, 255).astype(np.uint8)
            self.title_labels[5].config(text="6. Внешнее оконтуривание (Задание 4, Вар. 5)")

        # Отображение всех 6 изображений
        images_to_show = [
            self.orig_img_pil,
            Image.fromarray(self.gray_arr),
            Image.fromarray(self.bin_arr),
            Image.fromarray(z2_arr),
            Image.fromarray(z3_arr),
            Image.fromarray(z4_arr)
        ]

        for i, pil_im in enumerate(images_to_show):
            disp_im = pil_im.copy()
            disp_im.thumbnail((400, 310), Image.Resampling.NEAREST)
            tk_im = ImageTk.PhotoImage(disp_im)
            self.img_labels[i].config(image=tk_im)
            self.img_labels[i].image = tk_im


if __name__ == "__main__":
    root = tk.Tk()
    app = ImageProcessingApp(root)
    root.mainloop()