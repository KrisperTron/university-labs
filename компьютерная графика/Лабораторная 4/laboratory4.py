import tkinter as tk
from tkinter import filedialog, messagebox, ttk
from PIL import Image, ImageTk
import numpy as np
from collections import deque


class ImageProcessingApp:
    def __init__(self, root):
        self.root = root
        self.root.title("Лабораторная работа № 4 — Алгоритмы обработки изображений (с Заданием 5*)")
        self.root.geometry("1440x900")

        self.orig_img_pil = None
        self.gray_arr = None
        self.bin_arr = None
        self.edge_arr = None

        self.create_widgets()
        self.generate_default_image()

    def create_widgets(self):
        # Верхняя панель управления
        control_frame = tk.LabelFrame(self.root, text="Параметры преобразований", padx=10, pady=8)
        control_frame.pack(side=tk.TOP, fill=tk.X, padx=10, pady=5)

        # Кнопка загрузки изображения
        btn_load = tk.Button(
            control_frame, text="Загрузить изображение", command=self.load_image,
            bg="#dceeff", font=("Arial", 9, "bold"), padx=8, pady=4
        )
        btn_load.grid(row=0, column=0, rowspan=2, padx=8, pady=5)

        # Задание 1: Порог бинаризации
        tk.Label(control_frame, text="Задание 1 (Порог бинаризации 0-255):").grid(row=0, column=1, sticky="e", padx=4)
        self.thresh_bin_var = tk.IntVar(value=128)
        tk.Spinbox(control_frame, from_=0, to=255, textvariable=self.thresh_bin_var, width=5).grid(row=0, column=2, sticky="w", padx=4)

        # Задание 2 (Вариант 5): Порог неполной пороговой обработки
        tk.Label(control_frame, text="Задание 2, Вар. 5 (Порог неполной обр. 0-255):").grid(row=1, column=1, sticky="e", padx=4)
        self.thresh_z2_var = tk.IntVar(value=120)
        tk.Spinbox(control_frame, from_=0, to=255, textvariable=self.thresh_z2_var, width=5).grid(row=1, column=2, sticky="w", padx=4)

        # Задание 4 (Вариант 5): Внешнее оконтуривание — форма, размер ядра, якорь
        tk.Label(control_frame, text="Задание 4 (Форма ядра):").grid(row=0, column=3, sticky="e", padx=4)
        self.kernel_shape_var = tk.StringVar(value="Прямоугольник")
        shape_combo = ttk.Combobox(
            control_frame, textvariable=self.kernel_shape_var, state="readonly", width=14,
            values=["Прямоугольник", "Крест", "Ромб"]
        )
        shape_combo.grid(row=0, column=4, sticky="w", padx=4)

        tk.Label(control_frame, text="Размер ядра (W x H):").grid(row=1, column=3, sticky="e", padx=4)
        size_frame = tk.Frame(control_frame)
        size_frame.grid(row=1, column=4, sticky="w", padx=4)
        self.kw_var = tk.IntVar(value=3)
        self.kh_var = tk.IntVar(value=3)
        tk.Spinbox(size_frame, from_=1, to=15, textvariable=self.kw_var, width=4).pack(side=tk.LEFT)
        tk.Label(size_frame, text="x").pack(side=tk.LEFT)
        tk.Spinbox(size_frame, from_=1, to=15, textvariable=self.kh_var, width=4).pack(side=tk.LEFT)

        tk.Label(control_frame, text="Якорь (X, Y):").grid(row=0, column=5, sticky="e", padx=4)
        anchor_frame = tk.Frame(control_frame)
        anchor_frame.grid(row=0, column=6, sticky="w", padx=4)
        self.ax_var = tk.IntVar(value=1)
        self.ay_var = tk.IntVar(value=1)
        tk.Spinbox(anchor_frame, from_=0, to=14, textvariable=self.ax_var, width=4).pack(side=tk.LEFT)
        tk.Label(anchor_frame, text=",").pack(side=tk.LEFT)
        tk.Spinbox(anchor_frame, from_=0, to=14, textvariable=self.ay_var, width=4).pack(side=tk.LEFT)

        tk.Label(control_frame, text="Цвет объекта:").grid(row=1, column=5, sticky="e", padx=4)
        self.fg_color_var = tk.StringVar(value="Белый (255)")
        fg_combo = ttk.Combobox(
            control_frame, textvariable=self.fg_color_var, state="readonly", width=12,
            values=["Белый (255)", "Черный (0)"]
        )
        fg_combo.grid(row=1, column=6, sticky="w", padx=4)

        # Задание 5* (Вариант 1): Источник для алгоритма Зонга-Суена
        tk.Label(control_frame, text="Задание 5* (Вход Зонга-Суена):").grid(row=0, column=7, sticky="e", padx=4)
        self.z5_source_var = tk.StringVar(value="Контур из Задания 4")
        z5_combo = ttk.Combobox(
            control_frame, textvariable=self.z5_source_var, state="readonly", width=22,
            values=["Контур из Задания 4", "Бинарное из Задания 1"]
        )
        z5_combo.grid(row=0, column=8, sticky="w", padx=4)

        self.z5_info_lbl = tk.Label(control_frame, text="Связных контуров: -", fg="#004a99", font=("Arial", 9, "bold"))
        self.z5_info_lbl.grid(row=1, column=7, columnspan=2, sticky="w", padx=8)

        # Кнопка применения параметров
        btn_apply = tk.Button(
            control_frame, text="Применить / Обновить", command=self.process_all,
            bg="#d4f7d4", font=("Arial", 9, "bold"), padx=10, pady=4
        )
        btn_apply.grid(row=0, column=9, rowspan=2, padx=10, pady=5)

        # Область одновременного отображения 7 изображений
        self.grid_frame = tk.Frame(self.root)
        self.grid_frame.pack(side=tk.TOP, fill=tk.BOTH, expand=True, padx=10, pady=5)

        self.img_labels = []
        self.title_labels = []
        titles = [
            "1. Исходное изображение",
            "2. Оттенки серого (Задание 1)",
            "3. Пороговая обработка (Задание 1)",
            "4. Неполная пороговая (Задание 2, Вар. 5)",
            "5. Повышение контрастности (Задание 3, Вар. 5)",
            "6. Внешнее оконтуривание (Задание 4, Вар. 5)",
            "7. Алгоритм Зонга-Суена + связные контуры (Задание 5*, Вар. 1)"
        ]

        for i in range(7):
            r = i // 4
            c = i % 4
            cell = tk.Frame(self.grid_frame, bd=2, relief=tk.GROOVE, padx=4, pady=4)
            cell.grid(row=r, column=c, sticky="nsew", padx=4, pady=4)
            self.grid_frame.grid_columnconfigure(c, weight=1)
            self.grid_frame.grid_rowconfigure(r, weight=1)

            lbl_title = tk.Label(cell, text=titles[i], font=("Arial", 9, "bold"), wraplength=320)
            lbl_title.pack(side=tk.TOP, pady=2)
            self.title_labels.append(lbl_title)

            lbl_img = tk.Label(cell, bg="#ebebeb")
            lbl_img.pack(side=tk.TOP, fill=tk.BOTH, expand=True)
            self.img_labels.append(lbl_img)

        # 8-я ячейка — краткая памятка по параметрам текущего запуска
        info_cell = tk.Frame(self.grid_frame, bd=2, relief=tk.GROOVE, padx=10, pady=10, bg="#f9fbff")
        info_cell.grid(row=1, column=3, sticky="nsew", padx=4, pady=4)
        tk.Label(info_cell, text="Сводка по вариантам работы", font=("Arial", 10, "bold"), bg="#f9fbff").pack(anchor="w", pady=4)
        summary_text = (
            "• Задание 1: Оттенки серого и пороговая бинаризация.\n\n"
            "• Задание 2 (Вар. 5): Неполная пороговая обработка (рис. 1.в).\n\n"
            "• Задание 3 (Вар. 5): Масочная фильтрация (повышение контрастности, A=0, B=1, центр=5, края=-1).\n\n"
            "• Задание 4 (Вар. 5): Внешнее оконтуривание = Дилатация(A) \\ A.\n\n"
            "• Задание 5* (Вар. 1): Скелетизация Зонга-Суена до толщины в 1 пиксель и выделение отдельных 8-связных контуров разными цветами."
        )
        tk.Label(info_cell, text=summary_text, justify="left", wraplength=310, bg="#f9fbff", font=("Arial", 9)).pack(anchor="w")

    def generate_default_image(self):
        """Генерирует тестовое изображение с толстыми контурами и фигурами для наглядной проверки."""
        w, h = 280, 210
        arr = np.zeros((h, w, 3), dtype=np.uint8)
        # Градиентный темный фон
        for y in range(h):
            for x in range(w):
                arr[y, x] = [(x * 90) // w, (y * 90) // h, 80]

        # Прямоугольная рамка
        arr[25:115, 25:125] = [240, 240, 240]
        arr[45:95, 45:105] = [40, 40, 40]

        # Кольцо / круг
        yy, xx = np.ogrid[:h, :w]
        outer_circle = (xx - 200) ** 2 + (yy - 85) ** 2 <= 50 ** 2
        inner_circle = (xx - 200) ** 2 + (yy - 85) ** 2 <= 28 ** 2
        arr[outer_circle & ~inner_circle] = [255, 230, 80]

        # Т-образная толстая линия внизу
        arr[150:168, 40:230] = [235, 235, 235]
        arr[130:195, 125:145] = [235, 235, 235]

        self.orig_img_pil = Image.fromarray(arr)
        self.process_all()

    def load_image(self):
        path = filedialog.askopenfilename(
            title="Выберите изображение",
            filetypes=[("Images", "*.png;*.jpg;*.jpeg;*.bmp;*.tif"), ("All files", "*.*")]
        )
        if path:
            img = Image.open(path).convert("RGB")
            # Ограничиваем размер до ~320x240, чтобы итерации Зонга-Суена выполнялись мгновенно
            img.thumbnail((320, 240), Image.Resampling.LANCZOS)
            self.orig_img_pil = img
            self.process_all()

    # ЗАДАНИЕ 1 
    def to_grayscale(self, rgb_arr):
        gray = (
            0.299 * rgb_arr[:, :, 0] +
            0.587 * rgb_arr[:, :, 1] +
            0.114 * rgb_arr[:, :, 2]
        )
        return np.clip(gray, 0, 255).astype(np.uint8)

    def threshold_binarize(self, gray_arr, threshold):
        return np.where(gray_arr >= threshold, 255, 0).astype(np.uint8)

    # ЗАДАНИЕ 2 (ВАРИАНТ 5)
    def incomplete_threshold(self, gray_arr, threshold):
        """Неполная пороговая обработка (рис. 1.в): при f(x,y) >= P -> 255, иначе f(x,y)."""
        return np.where(gray_arr >= threshold, 255, gray_arr).astype(np.uint8)
    
    # ЗАДАНИЕ 3 (ВАРИАНТ 5)
    def mask_filter_task3_var5(self, gray_arr):
        """
        Фильтр повышения контрастности: A = 0, B = 1, M = [[0, -1, 0], [-1, 5, -1], [0, -1, 0]].
        Края обрабатываются копированием яркости крайних рядов на внешний слой.
        """
        A = 0.0
        B = 1.0
        M = np.array([
            [0, -1,  0],
            [-1, 5, -1],
            [0, -1,  0]
        ], dtype=np.float32)

        padded = np.pad(gray_arr.astype(np.float32), pad_width=1, mode='edge')
        h, w = gray_arr.shape

        acc = np.zeros((h, w), dtype=np.float32)
        for i in range(3):
            for j in range(3):
                if M[i, j] != 0:
                    acc += padded[i:i + h, j:j + w] * M[i, j]

        res = A + B * acc
        return np.clip(res, 0, 255).astype(np.uint8)

    # ЗАДАНИЕ 4 (ВАРИАНТ 5)
    def build_structuring_element(self, kw, kh, shape_name):
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

    def external_contouring(self, bin_arr, kernel, ax, ay, fg_is_white=True):
        """
        Задание 4, Вариант 5: Внешнее оконтуривание.
        Выполняется над бинарным изображением: Дилатация(A) \\ A.
        """
        kh, kw = kernel.shape
        h, w = bin_arr.shape

        obj_mask = (bin_arr == 255) if fg_is_white else (bin_arr == 0)

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

        # Внешний контур — это пиксели, добавленные при расширении (дилатации), которых не было в исходном объекте
        ext_contour = dilated & (~obj_mask)

        if fg_is_white:
            return np.where(ext_contour, 255, 0).astype(np.uint8)
        else:
            return np.where(ext_contour, 0, 255).astype(np.uint8)

    # ЗАДАНИЕ 5* (ВАРИАНТ 1)
    def zhang_suen_thinning(self, bin_mask):
        """
        Алгоритм утончения (скелетизации) Зонга-Суена (Zhang-Suen).
        Принимает булеву/целочисленную матрицу (1 - объект, 0 - фон).
        Возвращает утонченную бинарную маску толщиной в 1 пиксель.
        """
        img = bin_mask.astype(np.uint8).copy()
        # Обнуляем граничную рамку в 1 пиксель для корректного анализа 8 соседей
        img[0, :] = 0
        img[-1, :] = 0
        img[:, 0] = 0
        img[:, -1] = 0

        while True:
            # Подытерация 1
            P1 = img[1:-1, 1:-1]
            P2 = img[0:-2, 1:-1]  # верх
            P3 = img[0:-2, 2:]    # верх-право
            P4 = img[1:-1, 2:]    # право
            P5 = img[2:, 2:]      # низ-право
            P6 = img[2:, 1:-1]    # низ
            P7 = img[2:, 0:-2]    # низ-лево
            P8 = img[1:-1, 0:-2]  # лево
            P9 = img[0:-2, 0:-2]  # верх-лево

            # B(P1): число ненулевых соседей
            B = P2 + P3 + P4 + P5 + P6 + P7 + P8 + P9

            # A(P1): число переходов 0 -> 1 при обходе по часовой стрелке P2..P9..P2
            A = (
                ((P2 == 0) & (P3 == 1)).astype(np.uint8) +
                ((P3 == 0) & (P4 == 1)).astype(np.uint8) +
                ((P4 == 0) & (P5 == 1)).astype(np.uint8) +
                ((P5 == 0) & (P6 == 1)).astype(np.uint8) +
                ((P6 == 0) & (P7 == 1)).astype(np.uint8) +
                ((P7 == 0) & (P8 == 1)).astype(np.uint8) +
                ((P8 == 0) & (P9 == 1)).astype(np.uint8) +
                ((P9 == 0) & (P2 == 1)).astype(np.uint8)
            )

            cond1 = (
                (P1 == 1) &
                (B >= 2) & (B <= 6) &
                (A == 1) &
                ((P2 * P4 * P6) == 0) &
                ((P4 * P6 * P8) == 0)
            )
            if np.any(cond1):
                img[1:-1, 1:-1][cond1] = 0

            # Подытерация 2
            P1 = img[1:-1, 1:-1]
            P2 = img[0:-2, 1:-1]
            P3 = img[0:-2, 2:]
            P4 = img[1:-1, 2:]
            P5 = img[2:, 2:]
            P6 = img[2:, 1:-1]
            P7 = img[2:, 0:-2]
            P8 = img[1:-1, 0:-2]
            P9 = img[0:-2, 0:-2]

            B = P2 + P3 + P4 + P5 + P6 + P7 + P8 + P9
            A = (
                ((P2 == 0) & (P3 == 1)).astype(np.uint8) +
                ((P3 == 0) & (P4 == 1)).astype(np.uint8) +
                ((P4 == 0) & (P5 == 1)).astype(np.uint8) +
                ((P5 == 0) & (P6 == 1)).astype(np.uint8) +
                ((P6 == 0) & (P7 == 1)).astype(np.uint8) +
                ((P7 == 0) & (P8 == 1)).astype(np.uint8) +
                ((P8 == 0) & (P9 == 1)).astype(np.uint8) +
                ((P9 == 0) & (P2 == 1)).astype(np.uint8)
            )

            cond2 = (
                (P1 == 1) &
                (B >= 2) & (B <= 6) &
                (A == 1) &
                ((P2 * P4 * P8) == 0) &
                ((P2 * P6 * P8) == 0)
            )
            if np.any(cond2):
                img[1:-1, 1:-1][cond2] = 0

            if not np.any(cond1) and not np.any(cond2):
                break

        return img

    def extract_and_color_connected_contours(self, skeleton_mask):
        """
        Выделение 8-связных контуров на утонченном изображении (скелете) с помощью обхода в ширину (BFS)
        и раскраска каждого связного контура в свой яркий цвет.
        """
        h, w = skeleton_mask.shape
        visited = np.zeros((h, w), dtype=bool)
        color_out = np.zeros((h, w, 3), dtype=np.uint8)

        # Палитра ярких контрастных цветов для разных связных контуров
        palette = [
            (255, 60, 60),    # Красный
            (50, 220, 100),   # Зеленый
            (70, 160, 255),   # Голубой
            (255, 210, 50),   # Желтый
            (220, 90, 255),   # Фиолетовый
            (50, 235, 230),   # Бирюзовый
            (255, 140, 50),   # Оранжевый
            (255, 110, 190),  # Розовый
        ]

        neighbors_8 = [(-1, -1), (-1, 0), (-1, 1),
                       (0, -1),           (0, 1),
                       (1, -1),  (1, 0),  (1, 1)]

        contour_count = 0
        for r in range(h):
            for c in range(w):
                if skeleton_mask[r, c] == 1 and not visited[r, c]:
                    color = palette[contour_count % len(palette)]
                    contour_count += 1
                    queue = deque([(r, c)])
                    visited[r, c] = True

                    while queue:
                        cy, cx = queue.popleft()
                        color_out[cy, cx] = color
                        for dy, dx in neighbors_8:
                            ny, nx = cy + dy, cx + dx
                            if 0 <= ny < h and 0 <= nx < w:
                                if skeleton_mask[ny, nx] == 1 and not visited[ny, nx]:
                                    visited[ny, nx] = True
                                    queue.append((ny, nx))

        return color_out, contour_count

    def process_all(self):
        if self.orig_img_pil is None:
            return

        # Валидация ядра и якоря для Задания 4
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

        # 1. Оттенки серого (Задание 1)
        self.gray_arr = self.to_grayscale(rgb_arr)

        # 2. Пороговая обработка (Задание 1)
        self.bin_arr = self.threshold_binarize(self.gray_arr, thresh_bin)

        # 3. Неполная пороговая обработка (Задание 2, Вариант 5)
        z2_arr = self.incomplete_threshold(self.gray_arr, thresh_z2)

        # 4. Линейная масочная фильтрация (Задание 3, Вариант 5)
        z3_arr = self.mask_filter_task3_var5(self.gray_arr)

        # 5. Внешнее оконтуривание (Задание 4, Вариант 5)
        kernel = self.build_structuring_element(kw, kh, self.kernel_shape_var.get())
        fg_is_white = (self.fg_color_var.get() == "Белый (255)")
        self.edge_arr = self.external_contouring(self.bin_arr, kernel, ax, ay, fg_is_white)

        # 6. Утончение линий и выделение связных контуров (Задание 5*, Вариант 1)
        if self.z5_source_var.get() == "Контур из Задания 4":
            src_for_z5 = (self.edge_arr == 255) if fg_is_white else (self.edge_arr == 0)
        else:
            src_for_z5 = (self.bin_arr == 255) if fg_is_white else (self.bin_arr == 0)

        skeleton = self.zhang_suen_thinning(src_for_z5)
        z5_color_arr, num_contours = self.extract_and_color_connected_contours(skeleton)
        self.z5_info_lbl.config(text=f"Найдено связных контуров: {num_contours}")

        # Вывод всех 7 изображений на экран
        images_to_show = [
            self.orig_img_pil,
            Image.fromarray(self.gray_arr),
            Image.fromarray(self.bin_arr),
            Image.fromarray(z2_arr),
            Image.fromarray(z3_arr),
            Image.fromarray(self.edge_arr),
            Image.fromarray(z5_color_arr)
        ]

        for i, pil_im in enumerate(images_to_show):
            disp_im = pil_im.copy()
            disp_im.thumbnail((330, 260), Image.Resampling.NEAREST)
            tk_im = ImageTk.PhotoImage(disp_im)
            self.img_labels[i].config(image=tk_im)
            self.img_labels[i].image = tk_im


if __name__ == "__main__":
    root = tk.Tk()
    app = ImageProcessingApp(root)
    root.mainloop()